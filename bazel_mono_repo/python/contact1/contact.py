from flask import (
    Flask, redirect
)

app = Flask(__name__)

@app.route("/")
def index():
    return redirect("/contacts")

@app.route("/contacts")
def contacts():
    return "Hello redirect"