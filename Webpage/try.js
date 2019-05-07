data = (await d3.csv("https://raw.githubusercontent.com/d3/d3-hierarchy/v1.1.8/test/data/flare.csv", ({id, value}) => ({name: id.split(".").pop(), title: id.replace(/\./g, "/"), group: id.split(".")[1], value: +value})))
pack = data => d3.pack()
    .size([width - 2, height - 2])
    .padding(3)
  (d3.hierarchy({children: data})
    .sum(d => d.value))
width = 932
height = width
format = d3.format(",d")
color = d3.scaleOrdinal(data.map(d => d.group), d3.schemeCategory10)