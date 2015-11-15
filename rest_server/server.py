#!/usr/bin/env python
import web
import json 

# We expect a user_id param
# This serves as a simple identification,
# and authentication mechanism

urls = (
    '/signals/(.*)', 'handle_signals'
)

app = web.application(urls, globals())

# We check if the user_id presented in the 
# request matches one of the users in the 
# users file. If yes, we accept the posts
# from this user and store them in the signals
# file.

class handle_signals:
    def POST(self, user):
        pyDict = {}
        web.header('Content-Type', 'application/json')

	f = open('users', 'r')
	for line in f:
		if line.strip() == user.strip():
			postdata = web.data()	
			wf = open(user.strip() + '_signals', 'a')
			if len(postdata.strip()) > 0:
				wf.write(postdata + "\n")
			wf.close()
			f.close()
			pyDict['result']='success'
        		return json.dumps(pyDict)
			

	f.close();
	pyDict['result'] = "error. No user found."
       	return json.dumps(pyDict)
			

    def GET(self, user):
	return "Not yet implemented"


if __name__ == "__main__":
    app.run()
