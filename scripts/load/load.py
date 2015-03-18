import sqlite3
import random
import string
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

def generate_curl_patients():
	pass

def generate_curl_doctors():
	pass

def generate_curl_episodes():
	pass

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
			pass

	else:
		print 'Wrong usage. manual at: python load.py -h'


	connection.close()

	

if __name__ == '__main__':
	main()