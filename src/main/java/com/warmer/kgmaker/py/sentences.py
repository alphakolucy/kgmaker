# -*-coding:utf-8 -*-
# encoding:'utf8'
import json
from py2neo import Node, Relationship,Graph,NodeMatch,NodeMatcher,RelationshipMatcher
graph = Graph("http://localhost:7474", username="neo4j", password="booledata")
def SentenceJoint(words): #将词语拼接成句子
    k = 0
    sentence = []
    result = ''
    if words[len(words)-1] == '职工工伤与职业病致残等级':
        words.pop(len(words)-1)
        words.pop(len(words)-1)
        words.append('《劳动能力鉴定 职工工伤与职业病致残等级》')
    if words[len(words)-1] == '人体损伤程度鉴定标准':
        words.pop(len(words)-1)
        words.append('《人体损伤程度鉴定标准》')
    for word in words:
        if k != 0 and k != len(words)-1 and k % 2 == 0:
            sentence.append(word)
            sentence.append(',')
            sentence.append(word)
        elif word == '临床表现':
            sentence.append('的')
            sentence.append(word)
            sentence.append('为')
        elif word == '依据':
            sentence.append('的')
            sentence.append(word)
            sentence.append('是')
        elif word == '内容':
            sentence.append('的')
            sentence.append(word)
            sentence.append('包含了')
        elif word == '分类':
            sentence.append('包括')
        elif word == '包含':
            sentence.append(word)
            sentence.append('了')
        elif word == '原因与机制':
            sentence.append('的')
            sentence.append(word)
            sentence.append('是')
        elif word == '取决于':
            sentence.append(word)
        elif word == '法医学鉴定':
            sentence.append('的')
            sentence.append(word)
            sentence.append('包含')
        elif word == '症状与体征':
            sentence.append('的')
            sentence.append(word)
            sentence.append('包含了')
        elif word == '相关学说':
            sentence.append(word)
            sentence.append('包含')
        else:
            sentence.append(word)
        k = k+1
    sentence.insert(len(sentence), '。')
    for sen in sentence:
        result = result + sen
    print(result)
def ClearWord(t): #清除字符串中其他字符
    word = ''
    words = []
    for i in t:
        if '\u4e00' <= i <= '\u9fff':
            word = word + i
        else:
            words.append(word)
            word = ''
    while '' in words:
        words.remove('')
    return words
def GetNodesIdList(t): #获取起始节点和终止节点的id
    ida = []
    idb = []
    for i in t:
        if i['id(a)'] not in ida:
            ida.append(i['id(a)'])
        if i['id(b)'] not in idb:
            idb.append(i['id(b)'])
    return ida, idb
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
#查找图中节点信息

try:
    getnodesid = graph.run("Match (a),(b)where a.name contains('脊柱')   and b.name contains('损伤')  return id(a) , id(b)").data()
    alist, blist = GetNodesIdList(getnodesid)
    l, nodes = Length(alist, blist)
    for node in nodes:
        if node['length'] == min(l):
            r = graph.run("Match (a),(b) ,p = shortestpath((a)-[*..100]-(b)) where id(a) = %d and id(b) = %d  return p"%(node['id(a)'],node['id(b)'])).data()
            ids = graph.run("Match (a),(b) ,p = shortestpath((a)-[*..100]-(b)) where id(a) = %d and id(b) = %d   return a.name,a.detail,b.name,b.detail"%(node['id(a)'],node['id(b)'])).data()
            t = str(r[0]['p'])  # 将graph返回的内容转换为字符串
            if '->' in t and '<-' not in t:
                words = ClearWord(t)
                SentenceJoint(words)
                print(ids[0]['a.name'], ':', ids[0]['a.detail'])
                print(ids[0]['b.name'], ':', ids[0]['b.detail'])
            if '<-' in t and '->' not in t:
                words = ClearWord(t)
                words.reverse()
                SentenceJoint(words)
                print(ids[0]['a.name'], ':', ids[0]['a.detail'])
                print(ids[0]['b.name'], ':', ids[0]['b.detail'])
            # if '<-' in t and '->' in t:
            #     print(ids[0]['a.name'], ':', ids[0]['a.detail'])
            #     print(ids[0]['b.name'], ':', ids[0]['b.detail'])
except:
    print('输入错误！查无此词！请重新输入！')







