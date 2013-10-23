import webapp2
import json
import urllib2
import urllib

from google.appengine.ext import ndb
from datetime import datetime
from datetime import timedelta

class MainHandler(webapp2.RequestHandler):
    def get(self):
        self.response.write('Hello world!')


class User(ndb.Model):
    # username
	username = ndb.StringProperty(required=True)
	password = ndb.StringProperty(required=True)
	firstname = ndb.StringProperty(required=True)
	lastname = ndb.StringProperty(required=True)
	deviceID = ndb.StringProperty()
	licence = ndb.StringProperty()

	def toDict(self):
		user = {}
		user["username"] = self.username
		user["password"] = self.password
		user["firstname"] = self.firstname
		user["lastname"] = self.lastname
		user["licence"] = self.licence
		user["deviceID"] = self.deviceID
		return user
		
class Ride(ndb.Model):
	owner = ndb.StringProperty(required=True)
	startCity = ndb.StringProperty(required=True)
	destCity = ndb.StringProperty(required=True)
	dateTime = ndb.DateTimeProperty(required=True)
	availSeatNum = ndb.IntegerProperty(required=True)
	price = ndb.IntegerProperty(required=True)
	#participants = ndb.StringProperty(repeated=True)
	#passengers = ndb.StringProperty(repeated=True)
	
	def toDict2(self):
		ride = {}
		ride["id"] = self.key.id()
		ride["owner"] = self.owner
		ride["startcity"] = self.startCity
		ride["destcity"] = self.destCity
		ride["date"] = self.dateTime.strftime('%m/%d/%Y')
		ride["time"] = self.dateTime.strftime('%H:%M')
		ride["price"] = self.price
		ride["availSeatNum"] = self.availSeatNum
		#ride["participants"] = []
		#whole_join = Join.query(Join.rideID==self.key.id()).fetch()
		#ride["participants"] = self.participants
		#ride["passengers"] = self.passengers
		return ride
	
	def toDict(self):
		ride = self.toDict2()
		whole_join = Join.query(Join.rideID==self.key.id()).fetch()
		ride["participants"] = []
		for j in whole_join:
			ride["participants"].append(j.toDict2())
		return ride
		
class Join(ndb.Model):
	rideID = ndb.IntegerProperty(required=True)
	rsu_username = ndb.StringProperty(required=True)
	isAccepted = ndb.BooleanProperty(default=False)
	isRejected = ndb.BooleanProperty(default=False)
	isPaid = ndb.BooleanProperty(default=False)
	
	def toDict(self):
		join = Ride.get_by_id(self.rideID).toDict2()
		join["isAccepted"] = self.isAccepted
		join["isRejected"] = self.isRejected
		join["isPaid"] = self.isPaid
		return join
		
	def toDict2(self):
		join = {}
		join["rsu_username"] = self.rsu_username
		join["isAccepted"] = self.isAccepted
		join["isRejected"] = self.isRejected
		join["isPaid"] = self.isPaid
		return join
		
class Request(ndb.Model):
	username = ndb.StringProperty(required=True)
	startCity = ndb.StringProperty(required=True)
	destCity = ndb.StringProperty(required=True)
	dateTime = ndb.DateTimeProperty(required=True)
	
	def toDict(self):
		request = {}
		request["username"] = self.username
		request["id"] = self.key.id()
		return request
		

class LoginHandler(webapp2.RequestHandler):
	def post(self):
		username_data = self.request.get('username')#json.loads(self.request.body)
		password_data = self.request.get('password')
		deviceID_data = self.request.get('deviceID')
		user = User.query(User.username==username_data).get()
		if user == None:
			return self.response.out.write('1')
		if password_data != user.password:
			return self.response.out.write('2')
		for u in User.query(User.deviceID==deviceID_data).fetch():
			u.deviceID = ""
			u.put()
		user.deviceID = deviceID_data
		user.put()
		return self.response.out.write('3')
		
class PostRideHandler(webapp2.RequestHandler):
	def post(self):
		owner_data = self.request.get('owner')
		price_data = self.request.get('price')
		availSeatNum_data = self.request.get('availSeatNum')
		startCity_data = self.request.get('startcity')
		destCity_data = self.request.get('destcity')
		dateTime_data = datetime(int(self.request.get('year')), int(self.request.get('month')), int(self.request.get('date')), int(self.request.get('hour')), int(self.request.get('minute')), 0)
		if dateTime_data < datetime.now():
			return  self.response.out.write('2')
		ride = Ride(owner=owner_data, startCity=startCity_data, destCity=destCity_data, dateTime=dateTime_data, price=int(price_data), availSeatNum=int(availSeatNum_data))
		ride.put()
		search_dateTime = datetime(int(self.request.get('year')), int(self.request.get('month')), int(self.request.get('date')), 0, 0, 0)
		request2 = Request.query(Request.dateTime==search_dateTime, Request.destCity==destCity_data, Request.startCity==startCity_data, Request.username!=owner_data)
		if request2.get() == None:
			return self.response.out.write('1')
		else:
			result = []
			for r in request2.fetch():	
				result.append(r.toDict())
			#print result
			return self.response.out.write(json.dumps(result))
			#return  self.response.out.write('2')
		
		
class SignupHandler(webapp2.RequestHandler):
	def post(self):
		username_data = self.request.get('username')
		password_data = self.request.get('password')
		firstname_data = self.request.get('firstname')
		lastname_data = self.request.get('lastname')
		licence_data = self.request.get('licence')
		if User.query(User.username==username_data).get() != None:
			return self.response.out.write('1')
		else:
			user = User(username=username_data, password=password_data, firstname=firstname_data, lastname=lastname_data, licence=licence_data)
			user.put()
			return self.response.out.write(username_data)
			
class SearchRideHandler(webapp2.RequestHandler):
	def post(self):
		startCity_data = self.request.get('startcity')
		destCity_data = self.request.get('destcity')
		year_data = int(self.request.get('year'))
		month_data = int(self.request.get('month'))
		date_data = int(self.request.get('date'))
		username_data = self.request.get('username')
		dateTime_data = datetime(year_data, month_data, date_data, 0, 0, 0)
		rides = Ride.query(Ride.startCity==startCity_data, Ride.destCity==destCity_data, Ride.dateTime>=dateTime_data, Ride.dateTime<dateTime_data + timedelta(days=1)).order(Ride.dateTime).fetch()
		result = []
		for r in rides:
			if r.availSeatNum>0 and r.owner!=username_data:
				result.append(r.toDict())
		return self.response.out.write(json.dumps(result))
		
class RequestRideHandler(webapp2.RequestHandler):
	def post(self):
		startCity_data = self.request.get('startcity')
		destCity_data = self.request.get('destcity')
		year_data = int(self.request.get('year'))
		month_data = int(self.request.get('month'))
		date_data = int(self.request.get('date'))
		username_data = self.request.get('username')
		dateTime_data = datetime(year_data, month_data, date_data, 0, 0, 0)
		request = Request(username=username_data,startCity=startCity_data,destCity=destCity_data,dateTime=dateTime_data)
		request.put()
		return self.response.out.write('1')
		
class GetRideByIDHandler(webapp2.RequestHandler):
	def post(self):
		rideID_data = self.request.get('rideID')
		ride = Ride.get_by_id(int(rideID_data))
		return self.response.out.write(json.dumps([ride.toDict()]))
	
class GCMHandler(webapp2.RequestHandler):
	def get(self):
		data1={}
		regid = "APA91bFpyjYmhsBOiSeS63Yb1y6WpFnttq-pJkxb15gJ3BsdY-fGuiHmayT7zbOT0jwSGLavu7LhYWiUw2GD5VKAFIWTFim6VAHU27cYahdxnZsRHhL8NSOrW0Vpx0Z1FNBCCgCv6bfzHicpP3444jY_iiE21pqihw"
		data1['registration_id'] = regid
		data1['data'] ="test!"
		req = urllib2.Request("https://android.googleapis.com/gcm/send")
		req.add_header('Authorization','key=AIzaSyAdn5VkUfIw69nFydKjB1clGnWKSELhlOc')
		req.add_data(urllib.urlencode(data1))
		res = urllib2.urlopen(req)
		return self.response.out.write('massage sent')
		
class JoinHandler(webapp2.RequestHandler):
	def post(self):
		rideID_data = self.request.get('rideID')
		rsu_username_data = self.request.get('participant')
		ride = Ride.get_by_id(int(rideID_data))
		if ride.availSeatNum == 0 : return self.response.out.write('2')
		if Join.query(Join.rideID==int(rideID_data), Join.rsu_username==rsu_username_data).get() != None : return self.response.out.write('3')
		join = Join(rideID=int(rideID_data), rsu_username=rsu_username_data)
		if self.request.get('isInvited') == '1':
			join.isAccepted = True;
		join.put()
		#ride.participants.append(participant_data)
		#ride.availSeatNum = ride.availSeatNum - 1
		#ride.put()
		
		rou = User.query(User.username==ride.owner).get()
		regid = rou.deviceID
		notification = {}
		notification['registration_id'] = regid
		if self.request.get('isInvited') == '1':
			notification['data.message'] = rsu_username_data + " has accepted your invitation!"
			ride.availSeatNum -= 1
			ride.put()
		else:
			notification['data.message'] = rsu_username_data + " wants to join your ride!"
		
		#notification['potentialRSU'] = participant_data
		req = urllib2.Request("https://android.googleapis.com/gcm/send")
		req.add_header('Authorization','key=AIzaSyAdn5VkUfIw69nFydKjB1clGnWKSELhlOc')
		req.add_data(urllib.urlencode(notification))
		res = urllib2.urlopen(req)
		return self.response.out.write('1')

class ConfirmRideHandler(webapp2.RequestHandler):
	def post(self):
		result_data = self.request.get('result')
		rideID_data = int(self.request.get('rideID'))
		rsu_username_data = self.request.get('rsu_username')
		join = Join.query(Join.rsu_username==rsu_username_data, Join.rideID==rideID_data).get()
		rsu = User.query(User.username==rsu_username_data).get()
		regid = rsu.deviceID
		notification = {}
		notification['registration_id'] = regid
		if result_data == '1':
			ride = Ride.get_by_id(rideID_data)
			ride.availSeatNum -= 1
			ride.put()
			join.isAccepted = True
			notification['data.message'] = "Your ride request has been accepted!"
		else:
			join.isRejected = True
			notification['data.message'] = "Your ride request has been rejected!"
		join.put()
		req = urllib2.Request("https://android.googleapis.com/gcm/send")
		req.add_header('Authorization','key=AIzaSyAdn5VkUfIw69nFydKjB1clGnWKSELhlOc')
		req.add_data(urllib.urlencode(notification))
		res = urllib2.urlopen(req)
		return self.response.out.write('1')

class ShowMyOfferHandler(webapp2.RequestHandler):
	def post(self):
		username_data = self.request.get('username')
		rides = Ride.query(Ride.owner==username_data).order(Ride.owner).fetch()
		result = []
		for r in rides:	
			result.append(r.toDict())
		return self.response.out.write(json.dumps(result))
		
class ShowJoinedHandler(webapp2.RequestHandler):
	def post(self):
		username_data = self.request.get('username')
		#rides = Ride.query(ndb.OR(username_data==Ride.participants, username_data==Ride.passengers)).fetch()
		whole_join = Join.query(Join.rsu_username==username_data).fetch()
		result = []
		for j in whole_join:
			r = Ride.get_by_id(j.rideID)
			result.append(r.toDict())
		return self.response.out.write(json.dumps(result))
		
class PaymentHandler(webapp2.RequestHandler):
	def post(self):
		username_data = self.request.get('username')
		rideID_data = int(self.request.get('rideID'))
		join = Join.query(Join.rsu_username==username_data, Join.rideID==rideID_data).get()
		join.isPaid = True
		join.isAccepted = False
		join.put()
		return self.response.out.write('1')
		
class LogoutHandler(webapp2.RequestHandler):
	def post(self):
		username_data = self.request.get('username')
		user = User.query(User.username==username_data).get()
		if user == None:
			return self.response.out.write('1')
		user.deviceID = ""
		user.put()
		return self.response.out.write('2')
		
class InviteHandler(webapp2.RequestHandler):
	def post(self):
		username_data = self.request.get('username')
		requestID_data = self.request.get('requestID')
		request = Request.get_by_id(int(requestID_data))
		dateTime_data = request.dateTime
		
		ride = Ride.query(Ride.startCity==request.startCity, Ride.destCity==request.destCity, Ride.dateTime>=dateTime_data, Ride.dateTime<dateTime_data + timedelta(days=1)).order(Ride.dateTime).get()
		
		rsu = User.query(User.username==request.username).get()
		regid = rsu.deviceID
		notification = {}
		notification['registration_id'] = regid
		notification['data.message'] = username_data + " invites you to thier ride!"
		notification['data.id'] = ride.key.id()
		notification['data.type'] = 1
		
		#notification['potentialRSU'] = participant_data
		req = urllib2.Request("https://android.googleapis.com/gcm/send")
		req.add_header('Authorization','key=AIzaSyAdn5VkUfIw69nFydKjB1clGnWKSELhlOc')
		req.add_data(urllib.urlencode(notification))
		res = urllib2.urlopen(req)
		
		return self.response.out.write('1')
		
class ContactHandler(webapp2.RequestHandler):
	def post(self):
		to_data = self.request.get('to')
		from_data = self.request.get('from')
		message_data = self.request.get('message')
		
		user = User.query(User.username==to_data).get()
		regid = user.deviceID
		
		notification = {}
		notification['registration_id'] = regid
		notification['data.message'] = from_data + " sent you a message regarding your ride"
		notification['data.type'] = 2
		notification['data.from_user'] = from_data
		notification['data.message_data'] = message_data
		
		req = urllib2.Request("https://android.googleapis.com/gcm/send")
		req.add_header('Authorization','key=AIzaSyAdn5VkUfIw69nFydKjB1clGnWKSELhlOc')
		req.add_data(urllib.urlencode(notification))
		res = urllib2.urlopen(req)
		return self.response.out.write('1')

app = webapp2.WSGIApplication([('/', MainHandler), ('/login', LoginHandler),
	('/signup', SignupHandler), ('/postride', PostRideHandler), ('/searchride', SearchRideHandler),
	('/gcm', GCMHandler), ('/join', JoinHandler), ('/getridebyid', GetRideByIDHandler),
	('/mypostedrides', ShowMyOfferHandler), ('/myjoinedrides', ShowJoinedHandler), 
	('/confirmride', ConfirmRideHandler), ('/payment', PaymentHandler),
	('/logout', LogoutHandler), ('/requestride', RequestRideHandler),
	('/invite', InviteHandler), ('/contact', ContactHandler)], debug=True)





