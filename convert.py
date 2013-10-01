from xml.dom import minidom
xmldoc = minidom.parse('map2.svg')
itemlist = xmldoc.getElementsByTagName('rect')

walls = []
blinks = []
goals = []

def get_type(item):
   label = item.attributes.get('inkscape:label')
   if label and label.value == 'goal':
      return goals
   elif label and label.value == 'blink':
      return blinks
   else:
      return walls

for item in xmldoc.getElementsByTagName('rect'):
   x1 = int(float(item.attributes['x'].value))
   y1 = int(float(item.attributes['y'].value))
   w = int(float(item.attributes['width'].value))
   h = int(float(item.attributes['height'].value))

   x2 = x1 + w
   y2 = y1

   x3 = x2
   y3 = y2 + h

   x4 = x2 - w
   y4 = y3
   t = get_type(item)
   t.append("new Polygon(new int[]{%d, %d, %d, %d}, new int[]{%d, %d, %d, %d}, 4)," % (x1, x2, x3, x4, y1, y2, y3, y4))

import re
pattern = re.compile("translate\\(([^,]+),([^\\)]+)\\)")
for item in xmldoc.getElementsByTagName('path'):
   d = item.attributes['d'].value
   xs = []
   ys = []
   ox = 0
   oy = 0
   t = item.attributes.get('transform')
   if t:
      m = pattern.match(t.value)
      if m:
           ox = int(float(m.group(1)))
           oy = int(float(m.group(2)))

   cx = ox
   cy = oy
   rel = None
   for pair in d.split(' '):
      if pair == 'm':
          rel = True
      if pair == 'M':
          rel = False
      p = pair.split(',')
      if isinstance(p, list) and len(p) == 2:
         if rel is None: raise Exception(d)
         if rel:
            cx += int(float(p[0]))
            cy += int(float(p[1]))
         else:
            cx = ox + int(float(p[0]))
            cy = oy + int(float(p[1]))
         xs.append(str(cx))
         ys.append(str(cy))
   t = get_type(item)
   t.append("new Polygon(new int[]{%s}, new int[]{%s}, %d)," % (','.join(xs), ','.join(ys), len(xs)))
   
print "private final Polygon[] walls = {"
for wall in walls:
    print wall
print "};"

print "private final Polygon[] blinks = {"
for wall in blinks:
    print wall
print "};"

print "private final Polygon[] goals = {"
for goal in goals:
    print goal
print "};"

