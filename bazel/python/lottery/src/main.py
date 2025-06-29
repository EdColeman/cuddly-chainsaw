import ssl
import urllib.request

url = "https://data.ny.gov/api/views/5xaw-6ayf/rows.json?accessType=DOWNLOAD"

filename = "../data/mm.json"

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE
# ctx.verify_mode = ssl.CERT_OPTIONAL

# context=ssl.SSLContext()
with urllib.request.urlopen(url, context=ctx) as data:
    print(data.read())

