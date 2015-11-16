REST Server
===========

This is a simple python web.py based REST Server that accepts POST requests from clients

1. `/user/(application_key)` Responds in JSON format {"result" : "success"}, if the provided `application_key` is valid else returns {"result" : "error. No user found."}
2. `/signals/(application_key)` Request with the POST body containing the JSON of the signals of various access_points. For this request, the script simply appends the contents of the JSON to a text file with the name `(application_key)_signals`.


The web server is based on web.py framework. Hence, the requirements for running this script are:

- A python environment (Tested on Python 2.7.3)
- web.py 
```
	sudo easy_install web.py
```
- json
 

### Files needed 

- [users]() file in the project directory that contains the names of the users that can post to the server


Starting the server

```python

	python server.py 1234
		
```
This starts the server on port 1234.	 
