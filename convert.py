from xml.dom import minidom
xmldoc = minidom.parse('map.svg')
itemlist = xmldoc.getElementsByTagName('rect')
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

   print "new Polygon(new int[]{%d, %d, %d, %d}, new int[]{%d, %d, %d, %d}, 4)," % (x1, x2, x3, x4, y1, y2, y3, y4)

