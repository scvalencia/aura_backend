import itertools
import sqlite3
import random
import string
import json
import sys

'''
	Defines local interaction with the local database.
	This database, is in sqlite, and has two tables, one
	for patients, and one for doctors. The idea of this
	database, is to hold data for loading consistent chunks
	of data into the development and production database
	in postgreSQL, the final goal of this program, is to
	generate dependent shell files with curl commands to
	send data via HTTP/REST to the production or development
	databases. The production database is on Heroku, while
	the development database in on locahost powered by Play!
	network facilities
'''

db_name = 'load.db'
connection = sqlite3.connect(db_name)

remote_url = 'https://aura-prod.herokuapp.com/'
local_url = 'http://localhost:9000/'

def print_info():
	chains = []
	chains.append('\n\nload: A tool for database administration at aura (internal usage).\n')
	chains.append('\n')

	chains.append('\tload is a tool to generate consistent input data to insert data\n')
	chains.append('\tinto the aura databases (production, prototype, development). load\n')
	chains.append('\ttakes as arguments an entity to generate data from, a place to bulk\n')
	chains.append('\tdata into (production, development, prototyping); an a max bound\n')
	chains.append('\tto count the rows to generate.\n')

	chains.append('\n')

	chains.append('\tIf the database is the prototype database (sqlite), load, is capable\n')
	chains.append('\tof insert data directly to the database. If the database is the\n')
	chains.append('\tproduction or development databse, located in Heroku, ans localhost\n')
	chains.append('\trespectively, load should generate shell scripts to POST data to the services\n')
	chains.append('\tlayer of the app.\n')

	chains.append('\n')

	chains.append('\tload is a custom command line program, if the entity, the protocol or the\n')
	chains.append('\tdatabase changes, load should be uploaded.\n')

	chains.append('\n')

	chains.append('SYNTAX:\n\n')

	chains.append('\tpython load.py -h\n')
	chains.append('\t\tprints this message\n\n')

	chains.append('\tpython load.py (-p | -d | -e) (-l | -r) (%d*) \n')
	chains.append('\t\tGenerates shell scripts for the given entity, at localhost or remote stage.\n')
	chains.append('\t\tThe shell script is for POSTING objects to the app using curl.\n')
	chains.append('\t\t-p is for patient, -d is for doctor, -e is for episodes. If the -p, or -d\n')
	chains.append('\t\tflags are picked, %d is an integer to show how many objects do you want to send.\n')
	chains.append('\t\tOtherwise, if -e is selected, a script would be generated to POST at maximum %d\n')
	chains.append('\t\tepisodes for each patient in the respective database.\n\n')

	chains.append('\tpython load.py -sc (-p | -d) (%d*) \n')
	chains.append('\t\tLoads to sqlite3 the tables doctor (-d), or patient (-p), with %d* rows.\n\n')

	chains.append('\tpython load.py -sl (-p | -d) \n')
	chains.append('\t\tRemoves the data in sqlite3 from the table doctor (-d), or patient (-p).\n\n')

	print ''.join(chains)

def create_tables():
	qry = open('load.sql', 'r').read()
	queries = [i.strip() for i in qry.split(';')]
	c = connection.cursor()
	for q in queries:
		c.execute(q)
	connection.commit()
	c.close()

def set_names():
	global names
	file_object = open('first.txt', 'r')
	names = [_.strip() for _ in file_object.readlines()]

def set_last_names():
	global last_names
	file_object = open('last.txt', 'r')
	last_names = [_.strip() for _ in file_object.readlines()]

def pick_name():
	name = random.choice(names).capitalize() + ' ' + random.choice(last_names)
	return name

def pick_date():
	ans = ''
	year = range(1930, 2010)
	month = ['0' + str(i) for i in range(1, 10)] + ['10', '11', '12']
	day = ['0' + str(i) for i in range(1, 10)] + [str(i) for i in range(10, 28)]
	ans += str(random.choice(year)) + '-' + random.choice(month) + '-' + random.choice(day)
	return ans

def pick_email(name):
	name = name.split()
	email = name[0][0].lower() + name[1].lower()
	email += str(random.randrange(100, 999))
	email += '@gmail.com'
	return email

def create_patient():

	patient_id = '0'
	while patient_id[0] == '0':
		patient_id = ''.join(random.choice(string.digits) for _ in range(11))

	name = pick_name()
	password = ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(8))
	birth_date = pick_date()
	email = pick_email(name)
	gender = random.choice(['0', '1'])

	return (patient_id, name, password, birth_date, email, gender)

def insert_patients(bound):

	c = connection.cursor()
	
	for i in range(bound):
		patient = create_patient()
		c.execute('INSERT INTO patient VALUES (?, ?, ?, ?, ?, ?)', patient)
		connection.commit()

	c.close()

def create_doctor():

	doctor_id = '0'
	while doctor_id[0] == '0':
		doctor_id = ''.join(random.choice(string.digits) for _ in range(11))

	gender = random.choice(['0', '1'])
	name = pick_name()
	email = pick_email(name)
	birth_date = pick_date()
	password = ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(8))
	discipline = random.choice([str(i) for i in range(0, 10)])
	link = 'www.linkedin.com/' + str(doctor_id)	

	return (doctor_id, gender, name, email, birth_date, password, discipline, link)

def insert_doctors(bound):

	c = connection.cursor()
	
	for i in range(bound):
		doctor = create_doctor()
		c.execute('INSERT INTO doctor VALUES (?, ?, ?, ?, ?, ?, ?, ?)', doctor)
		connection.commit()

	c.close()


def display():
	'.mode column'
	'.headers ON'

def load_localSQLite_data(entity, bound):
	set_names()
	set_last_names()

	if entity == '-p':
		insert_patients(bound)
	elif entity == '-d':
		insert_doctors(bound)

def clear_localSQLite_data():
	c = connection.cursor()
	c.execute('DELETE FROM doctor')
	c.execute('DELETE FROM patient')
	connection.commit()
	c.close()

def generate_curl_patients(bound, local):
	
	class Patient(object):

		def __init__(self, arg_id, name, email, password, date, gender):

			self.id = arg_id
			self.name = name
			self.email = email
			self.password = password
			self.date = date
			self.gender = gender

		def get_json(self):
			return json.dumps(self, default=lambda o: o.__dict__)

	c = connection.cursor()
	c.execute('SELECT * FROM patient')
	patients = [_ for _ in c.fetchall()]

	if bound > len(patients):
		print 'Not enough patients to write the file: execute python load.py -sc -p ' + str(bound - len(patients))
		print 'Then, execute python load.py -p (-l | -r) ' + str(bound)
		return

	random.shuffle(patients)

	used_ids = open('patients.txt', 'a')
	post_file = open('POST_patient.sh', 'w')

	i = 0
	url = local_url if local else remote_url
	service = 'api/patient'

	for itm in patients:

		itm_id = itm[0]
		itm_name = str(itm[1])
		itm_pass = str(itm[2])
		itm_date = str(itm[3])
		itm_mail = str(itm[4])
		itm_gender = str(itm[5])

		patient_object = Patient(itm_id, itm_name, itm_mail, itm_pass, itm_date, itm_gender)

		line = 'curl -v -H "Content-type: application/json" -X POST -d '
		line += "'" + patient_object.get_json() + "'" + ' '
		line += url + service
		line += '\n'

		post_file.write(line)
		used_ids.write(str(itm_id) + '\n')

		if i == bound:
			break

		i += 1

	c.close()			

def generate_curl_doctors(bound, local):
	
	class Doctor(object):

		def __init__(self, arg_id, gender, name, email, date, password, discipline):

			self.id = arg_id
			self.gender = gender
			self.name = name
			self.email = email
			self.date = date
			self.password = password
			self.discipline = discipline

		def get_json(self):
			return json.dumps(self, default=lambda o: o.__dict__)

	c = connection.cursor()
	c.execute('SELECT * FROM doctor')
	doctors = [_ for _ in c.fetchall()]

	if bound > len(doctors):
		print 'Not enough doctors to write the file: execute python load.py -sc -d ' + str(bound - len(doctors))
		print 'Then, execute python load.py -d (-l | -r) ' + str(bound)
		return

	random.shuffle(doctors)

	post_file = open('POST_doctor.sh', 'w')

	i = 0
	url = local_url if local else remote_url
	service = 'api/doctor'

	for itm in doctors:

		d = Doctor(itm[0], itm[1], itm[2], itm[3], itm[4], itm[5], itm[6])
		link = itm[7]

		line = 'curl -v -H "Content-type: application/json" -X POST -d '
		line += "'" + d.get_json() + "'" + ' '
		line += url + service
		line += '\n'

		new_line = 'curl -v -H "Content-type: application/json" -X POST -d '
		new_line += "'" + json.dumps({"link" : str(link)}) + "'" + ' '
		new_line += url + service + '/' + str(itm[0]) + '/' + 'link' + '\n' 

		post_file.write(line)
		post_file.write(new_line)

		if i == bound:
			break

		i += 1
		

	c.close()

def generate_curl_episodes(bound, local):
	# Get patients from file, if file don't exist inform, otherwise, execute

	foods = ['Frijol', 'Chocolate', 'Cafe', 'Arroz', 'Sopa', 'Dulces', 'Arequipe',
	'Gaseosa', 'Te', 'Carne', 'Pescado', 'Pollo']

	medicines = ['Dolex', 'Morfina', 'Loratadina', 'Acetaminofen']

	class Episode(object):

		def __init__(self, url, intensity, sleep, regular, location, stress):
			self.urlId = url
			self.intensity = intensity
			self.sleepHours = sleep
			self.regularSleep = regular
			self.location = location
			self.stress = stress
			self.symptoms = []
			self.foods = []
			self.sports = []
			self.medicines = []
			
		def add_symtom(self, s):
			self.symptoms.append(s)

		def add_food(self, f):
			self.foods.append(f)

		def add_sport(self, s):
			self.sports.append(s)

		def add_medicine(self, m):
			self.medicines.append(m)

		def get_json(self):
			return json.dumps(self, default=lambda o: o.__dict__)

	class Symptom(object):

		def __init__(self):
			self.symptom = random.choice(range(1, 11))

	class Food(object):

		def __init__(self):
			self.name = random.choice(foods)
			self.quentity = random.choice(range(1, 11))

	class Medicine(object):

			def __init__(self):
				self.name = random.choice(medicines)
				self.hoursAgo = random.choice(range(0, 13))

	class Sport(object):

		def __init__(self):
			self.description = random.choice(range(1, 6))
			self.intensity = random.choice(range(1, 11))
			self.place = random.choice(range(1, 6))
			self.climate = random.choice(range(1, 6))
			self.hydration = random.choice([True, False])


	patients = set([i.strip() for i in open('patients.txt', 'r').readlines()])
	patients = list(patients)

	post_file = open('POST_episode.sh', 'w')
	url = local_url if local else remote_url
	service = lambda x : 'api/patient/' + str(x) + '/episode' 

	for patient in patients:

		max_bound = random.choice(range(bound + 1))
		i = 0
		while i <= max_bound:

			e = None
			
			soundCloud = random.choice([True, False])

			if soundCloud:
				sound_url = ''.join(random.choice(string.digits) for _ in range(8))
				intensity = 10
				sleep = ''
				regular = ''
				location = ''
				stress = ''

				e = Episode(sound_url, intensity, sleep, regular, location, stress)

			else:
				sound_url = ''
				intensity = random.choice(range(1, 11))
				sleep = random.choice(range(1, 13))
				regular = random.choice([True, False])
				location = random.choice(range(1, 6))
				stress = random.choice([True, False])

				e = Episode(sound_url, intensity, sleep, regular, location, stress)

				num_symptoms = random.choice(range(10))
				num_foods = random.choice(range(10))
				num_medicines = random.choice(range(10))
				num_sports = random.choice(range(10))

				j = 0
				while j <= num_symptoms:
					e.add_symtom(Symptom())
					j += 1

				j = 0
				while j <= num_foods:
					e.add_food(Food())
					j += 1

				j = 0
				while j <= num_medicines:
					e.add_medicine(Medicine())
					j += 1

				j = 0
				while j <= num_sports:
					e.add_sport(Sport())
					j += 1

			line = 'curl -v -H "Content-type: application/json" -X POST -d '
			line += "'" + e.get_json() + "'" + ' '
			line += url + service(patient)
			line += '\n'

			post_file.write(line)

			i += 1

def main():

	args = sys.argv[1:]

	if len(args) == 1:
		if args[0] == '-h':
			print_info()

		else:
			print 'Wrong usage. manual at: python load.py -h'

	elif len(args) == 2:
		if args[0] == '-sl' and args[1] in ['-d', '-p']:
			clear_localSQLite_data()

		else:
			print 'Wrong usage. manual at: python load.py -h'

	elif len(args) == 3:
		if args[0] == '-sc' and args[1] in ['-d', '-p'] and args[2].isdigit():
			entity = args[1]
			bound = int(args[2])

			load_localSQLite_data(entity, bound)

		elif args[0] in ['-d', '-p', '-e'] and args[1] in ['-l', '-r'] and args[2].isdigit():

			local = True if args[1] == '-l' else False
			bound = int(args[2])

			if args[0] == '-p':

				generate_curl_patients(bound, local)

			elif args[0] == '-e':

				generate_curl_episodes(bound, local)

			elif args[0] == '-d':

				generate_curl_doctors(bound, local)

		else:
			print 'Wrong usage. manual at: python load.py -h'

	else:
		print 'Wrong usage. manual at: python load.py -h'


	connection.close()

	

if __name__ == '__main__':
	main()