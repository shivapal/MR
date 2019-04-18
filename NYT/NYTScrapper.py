from nytimesarticle import articleAPI
import requests
from bs4 import BeautifulSoup
import csv
import urllib

APIKey = "5GDjPzkz28HHyX3LuEoWeAMiBUZGqnHw"
api = articleAPI (APIKey)

def read(): 
    # Read csv and return list of url
    url = []
    try:
        with open("url.csv", mode="r") as csv_file: 
            readCSV = csv.reader(csv_file, delimiter = ',')
            header = True
            for row in readCSV:
                if header:
                    header= False 
                    continue
                try:
                    url.append(row[0])
                except:
                    break
    except FileNotFoundError:
        with open("url.csv", mode = 'w') as csv_file:
            writer = csv.writer(csv_file, delimiter = ',')
            url = read()
    return url

def write(link):
    with open("url.csv", mode = 'a', newline = '\n') as csv_file:
        writer = csv.writer(csv_file, delimiter = ',')
        writer.writerow([link])

def getUrl(keyWord, page):
    url = []
    while(True):
        articles = api.search(q=keyWord, begin_date = 20190101, page = page)
        if not articles: break
        try:
            docs = articles["response"]["docs"]
        except KeyError:
            break
        for art in docs:
            link = art["web_url"] 
            url.append(link)
        if len(docs)==0: break
        page+=1
    print(len(url))
    return url

def getText(link):
    t = []
    page = requests.get(link)
    soup = BeautifulSoup(page.content, "html.parser")
    articleTitle = soup.title.string
    body = soup.findAll("p", {"class": "css-1ygdjhk evys1bk0"})
    # s = articleTitle + "\n"
    t.append(articleTitle)
    for text in body:
        m = text.get_text()
        # s += m + "\n"
        t.append(m)
    return t
    
def scrapURL():
    url = read() 
    text = [getText(link) for link in url]
    with open("nyttext.csv", mode = 'w', newline='\n', encoding = "utf-8-sig") as file:
        writer = csv.writer(file, delimiter = ',')
        writer.writerow(["Text"])
        for (link, string) in zip(url, text):
            writer.writerow(string)
    file.close()

def updateUrl(keyWord = "", page = 0):
    url = read()
    url = set(url)
    nyt = getUrl(keyWord, page)
    for link in nyt:
        if link not in url:
            write(link)

# updateUrl("sports", 0)
# updateUrl("basketball", 0)
# updateUrl("hockey", 0)
# updateUrl("tennis", 0)
# updateUrl("football", 0)
# updateUrl("soccer", 0)

scrapURL()
