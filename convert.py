from xml.dom import minidom
xmldoc = minidom.parse('map.svg')
itemlist = xmldoc.getElementsByTagName('rect')

walls = []
goals = []

for item in itemlist:
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
   else:
      t = walls
   t.append("new Polygon(new int[]{%d, %d, %d, %d}, new int[]{%d, %d, %d, %d}, 4)," % (x1, x2, x3, x4, y1, y2, y3, y4))

print "private final Polygon[] walls = {"
for wall in walls:
    print wall
print "};"

print "private final Polygon[] goals = {"
for goal in goals:
    print goal
print "};"

