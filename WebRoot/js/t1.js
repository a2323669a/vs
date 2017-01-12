//调整tension
//P′i=β⋅Pi+(1−β)(P0+(i/N−1)(PN−1-P0))
function myFunc(points, tension) {
	if(my_index > -1){
		if(my_index == 0){
		//	alert(my_count);
			if(my_count == 0){
				my_index = -1;
			}else{
				my_count--;
				tension = 0;
			}
		}else{
			my_index --;
		}
	}
	var n = points.length - 1, x0 = points[0][0], y0 = points[0][1], dx = points[n][0]
			- x0, dy = points[n][1] - y0, i = -1, p, t;
	while (++i <= n) {
		p = points[i];
		t = i / n;
		p[0] = tension * p[0] + (1 - tension) * (x0 + t * dx);
		
		p[1] = tension * p[1] + (1 - tension) * (y0 + t * dy);
	}
}

// 接受一个产生的links-imports
// 返回一个数组,数组里的元素是一个装有所有控制节点的数组
function myBundle(n) {
	for (var t = [], e = -1, r = n.length; ++e < r;)
		t.push(getPath(n[e]));// 对于每一个import,把它所要经过的节点加入
	return t
}
// 接受一个import{source,target}
// 返回一个数组,数组里是路径上的节点
function getPath(n) {
	for (var t = n.source, e = n.target, r = getLCA(t, e), i = [ t ]; t !== r;)
		t = t.parent, i.push(t);// 从起点返回所经过的节点-到LCA
	for (var u = i.length; e !== r;)
		i.splice(u, 0, e), e = e.parent;// 从终点返回所经过的节点-到LCA
	return i
}

// 接受一个起点,一个终点
// 返回一个节点,最小祖先,LCA
function getLCA(n, t) {
	if (n === t)
		return n;
	for (var e = getNodes(n), r = getNodes(t), i = e.pop(), u = r.pop(), o = null; i === u;)
		o = i, i = e.pop(), u = r.pop();
	return o
}

// 接受一个节点
// 返回一个数组,数组里是从这个点到根的所有节点
function getNodes(n) {
	for (var t = [], e = n.parent; null != e;)
		t.push(n), n = e, e = e.parent;
	return t.push(n), t
}
