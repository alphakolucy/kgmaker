


def GetNodesIdList(t): #获取起始节点和终止节点的id
    ida = []
    idb = []
    for i in t: #t是所有起始点和终止点的id
        if i['id(a)'] not in ida:
            ida.append(i['id(a)'])
        if i['id(b)'] not in idb:
            idb.append(i['id(b)'])
    return ida, idb
#alist = ida, blist = idb
def Length(alist , blist):# 遍历所有的起始节点和终止节点，得到最短路径长度
    l = []
    nodes = []
    for a in alist:
        for b in blist:
            if a != b: #当起始节点 ！= 终止节点时
                length = graph.run("Match (n),(m) ,p = shortestpath((n)-[*..100]-(m)) where id(n) = %d and id(m) = %d  return length(p)"%(a, b)).data()
                if len(length) != 0: #当路径存在时
                    new_node = {}
                    new_node['id(a)'] = a
                    new_node['id(b)'] = b
                    new_node['length'] = length[0]['length(p)']
                    nodes.append(new_node)
                    l.append(length[0]['length(p)'])
    return l, nodes