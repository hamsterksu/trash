from flask import Flask, request, Response
app = Flask(__name__)
 
@app.route("/gather.xml", methods=['GET', 'POST'])
def gather():
    xml = '<Response>'\
            '<Gather input="speech" action="http://127.0.0.1:5000/gather-action" partialResultCallback="http://127.0.0.1:5000/gather-action-partial" timeout="60">'\
                    '<Play>http://192.168.176.58:8080/restcomm/audio/demo-prompt.wav</Play>'\
                '</Gather>'\
                '<Say>After Gather</Say>'\
                '<Redirect>http://127.0.0.1:5000/say.xml</Redirect>'\
            '</Response>'
    return Response(xml, mimetype='text/xml')
 
@app.route("/gather-action", methods=['GET', 'POST'])
def gather_action():
    print(request.form)
    return "<Say>Thank you</Say>"


@app.route("/gather-action-partial", methods=['GET', 'POST'])
def gather_action_partial():
    print(request.form)
    return ""

@app.route("/say.xml", methods=['GET', 'POST'])
def end():
    return Response('<Response><Play>http://192.168.176.58:8080/restcomm/audio/demo-prompt.wav</Play></Response>', mimetype='text/xml')
 
app.run(host='0.0.0.0')
