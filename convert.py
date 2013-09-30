from xml.dom import minidom
xmldoc = minidom.parse('map.svg')
itemlist = xmldoc.getElementsByTagName('rect')

walls = []
blinks = []
goals = []

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

   label = item.attributes.get('inkscape:label')
   if label and label.value == 'goal':
      t = goals
   elif label and label.value == 'blink':
      t = blinks
   else:
      t = walls
   t.append("new Polygon(new int[]{%d, %d, %d, %d}, new int[]{%d, %d, %d, %d}, 4)," % (x1, x2, x3, x4, y1, y2, y3, y4))

for item in xmldoc.getElementsByTagName('path'):
   d = item.attributes['d'].value
   xs = []
   ys = []
   for pair in d.split(' '):
      p = pair.split(',')
      if isinstance(p, list) and len(p) == 2:
         xs.append(str(int(float(p[0]))))
         ys.append(str(int(float(p[1]))))
   walls.append("new Polygon(new int[]{%s}, new int[]{%s}, %d)," % (','.join(xs), ','.join(ys), len(xs)))
   
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

