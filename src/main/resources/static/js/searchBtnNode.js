var app = new Vue({
    el: '#app',
    data: {
        inputSource: '冻伤',
        inputTarget: '急性呼吸窘迫综合征',


        graph: {
            nodes: [],
            links: [],
        }

    },

    mounted() {
        // var data = this.data;
        // var inputSource = "";
        // var inputTarget = "";
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var str = '{ "' + header + '": "' + token + '"}';
        this.headers = eval('(' + str + ')');
        this.initgraph();


        // this.getBtnNode(inputSource,inputTarget);
    },
    created() {
        this.getlabels();
    },
    methods: {

        getmorenode() {
            var _this = this;
            var data = {domain: _this.domain, nodeid: _this.selectnodeid};
            $.ajax({
                data: data,
                type: "POST",
                url: contextRoot + "getmorerelationnode",
                success: function (result) {
                    if (result.code == 200) {
                        var newnodes = result.data.node;
                        var newships = result.data.relationship;
                        var oldnodescount = _this.graph.nodes.length;
                        newnodes.forEach(function (m) {
                            var sobj = _this.graph.nodes.find(function (x) {
                                return x.uuid === m.uuid
                            })
                            if (typeof (sobj) == "undefined") {
                                _this.graph.nodes.push(m);
                            }
                        })
                        var newnodescount = _this.graph.nodes.length;
                        if (newnodescount <= oldnodescount) {
                            _this.$message({
                                message: '没有更多节点信息',
                                type: 'success'
                            });
                            return;
                        }
                        newships.forEach(function (m) {
                            var sobj = _this.graph.links.find(function (x) {
                                return x.uuid === m.uuid
                            })
                            if (typeof (sobj) == "undefined") {
                                _this.graph.links.push(m);
                            }
                        })
                        _this.updategraph();
                    }
                },
                error: function (data) {
                }
            });
        },

        ticked() {
            linkspP.attr({
                "x1": function (d) {
                    return d.source.x;
                },
                "y1": function (d) {
                    return d.source.y;
                },
                "x2": function (d) {
                    return d.target.x;
                },
                "y2": function (d) {
                    return d.target.y;
                },
            });
            linksText.attr({
                "x": function (d) {
                    return (d.source.x + d.target.x) / 2;
                },
                "y": function (d) {
                    return (d.source.y + d.target.y) / 2;
                }
            })


        },

        getBtnNode() {
            var _this = this;
            _this.loading = true;
            var data = {
                inputSource: _this.inputSource,
                inputTarget: _this.inputTarget,
                nodes: _this.nodes,
                links: _this.links,
            };
            alert("data" + data.toString())
            $.ajax({
                data: data,
                type: "GET",
                url: contextRoot + "getBtnRelationship" + "/" + data.inputSource + "/" + data.inputTarget,
                success: function (result) {
                    if (result.code == 200) {
                        _this.graph.nodes = result.data.nodes;
                        alert("nodes" + nodes)
                        _this.graph.links = result.data.links;
                        alert("links" + links)
                    }
                }
            })
        },

        getlabels() {
            var _this = this;
            var data = {};
            $.ajax({
                data: data,
                type: "POST",
                //url: contextRoot+"getlabels",
                url: contextRoot + "getgraph",
                success: function (result) {
                    if (result.code == 200) {
                        //_this.domainlabels=result.data;
                        _this.pageModel = result.data;
                        _this.pageModel.totalPage = parseInt((result.data.totalCount - 1) / result.data.pageSize) + 1
                    }
                }
            });
        },


        initgraph() {


            var _this = this;


            var nodes = [{name: "冻伤", id: 0}, {name: "活体损伤", id: 1}, {name: "损伤并发症", id: 2}, {
                name: "急性呼吸窘迫综合征",
                id: 3
            }];


            var links = [{source: 1, target: 0}, {source: 1, target: 2}, {source: 2, target: 3}];

            var graphcontainer = d3.select(".graphcontainer2");
            var width = graphcontainer._groups[0][0].offsetWidth;
            var height = window.screen.height;
            var svg1 = d3.select(".graphcontainer2")
                .append("svg")
                .attr("width", width)
                .attr("height", height);
            // .attr("class", "svgreship");
            console.log(d3.forceManyBody());
            // 通过布局来转换数据，然后进行绘制
            var simulation = d3.forceSimulation(nodes)
                .force("link", d3.forceLink(links).distance(150))
                .force("charge", d3.forceManyBody())//创建多体力
                .force("center", d3.forceCenter(width / 2, height / 2));
            simulation
                .nodes(nodes)//设置力模拟的节点
                .on("tick", ticked);

            simulation.force("link")//添加或移除力
                .links(links);//设置连接数组
            var color = d3.scaleOrdinal(d3.schemeCategory20);
            // 绘制
            var svg1_links = svg1.selectAll("line")
                .data(links)
                .enter()
                .append("line")
                .style("stroke", "#ccc")
                .style("stroke-width", 2)
                .call(d3.zoom()//创建缩放行为
                    .scaleExtent([-5, 2])//设置缩放范围
                );

            console.log("转换后的nodes links数据:");
            console.log(nodes);
            console.log(links);

            var svg1_nodes = svg1.selectAll("circle")
                .data(nodes)
                .enter()
                .append("circle")
                .attr("cx", function (d) {
                    return d.x;
                })
                .attr("cy", function (d) {
                    return d.y;
                })
                .attr("r", '20')
                .attr("fill", function (d, i) {
                    return color(i);
                }).call(d3.drag().on("start", dragstarted)//d3.drag() 创建一个拖曳行为
                    .on("drag", dragged)
                    .on("end", dragended));
            //添加描述节点的文字
            var svg1_texts = svg1.selectAll("text")
                .data(nodes)
                .enter()
                .append("text")
                .style("fill", "black")
                .attr("dx", 20)
                .attr("dy", 8)
                .text(function (d) {
                    return d.name;
                });


            function dragstarted(d) {
                if (!d3.event.active) simulation.alphaTarget(0.3).restart();//设置目标α
                d.fx = d.x;
                d.fy = d.y;
            }

            function dragged(d) {
                d.fx = d3.event.x;
                d.fy = d3.event.y;
            }

            function dragended(d) {
                if (!d3.event.active) simulation.alphaTarget(0);
                d.fx = null;
                d.fy = null;
            }

            function ticked() {
                svg1_links.attr("x1", function (d) {
                    return d.source.x;
                })
                    .attr("y1", function (d) {
                        return d.source.y;
                    })
                    .attr("x2", function (d) {
                        return d.target.x;
                    })
                    .attr("y2", function (d) {
                        return d.target.y;
                    });

                svg1_nodes.attr("cx", function (d) {
                    return d.x;
                })
                    .attr("cy", function (d) {
                        return d.y;
                    });

                svg1_texts.attr("x", function (d) {
                    return d.x;
                })
                    .attr("y", function (d) {
                        return d.y;
                    });
            }

        }

    },
});

//







