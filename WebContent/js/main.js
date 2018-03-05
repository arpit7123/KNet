/*$(document).ready(function() {
	$('ul#examples li').click(function() {
		$('div#information').hide();
		$("#svg-canvas").empty();
		$("#svg-canvas1").empty();
		$(this).removeClass("deselected").addClass("selected");
		var text = $(this).text();
		var useCoreference = $('.chk').is(":checked");
		$("#sentence_input").val(text);
		$('body').css('cursor', 'progress');
		submitText(text, useCoreference);
	});
});*/

var globalData = null;

$(document).ready(function() {
//	$("#thelist").hide();
	$("#submit").click(function() {
		$('ul #thelist').empty();
		var event1Field = $("#event1Value");
		var event1 = event1Field.val();
		var event1SlotField = $("#event1SlotValue");
		var event1Slot = event1SlotField.val();
		var relationField = $("#relationValue");
		var relation = relationField.val();
		var event2Field = $("#event2Value");
		var event2 = event2Field.val();
		var event2SlotField = $("#event2SlotValue");
		var event2Slot = event2SlotField.val();
		var nlQueryField = $("#inputQuery");
		var nlQuery = nlQueryField.val();

		submitText(event1,event1Slot,relation,event2,event2Slot,nlQuery);
		return false;
	});

/*	$("#showList").click(function() {
		$("#thelist").show();
//		$("li#sentence1").show();
		return false;
	});

	$("#hideList").click(function() {
		$("#thelist").hide();
		return false;
	});

	$("#changeLabel").click(function() {
				$("#sentence1").html("Done");
		$("#sentence1").css('width', (0.9*100)+'%');
		return false;

		var node=document.createElement("li");
		node.setAttribute("class","progress");
		node.classList.add("progressBar-modified");
		node.classList.add("deselected");

		var node1=document.createElement("div");
		node1.setAttribute("class","progress-bar");
		node1.classList.add("progress-bar-striped");
		node1.classList.add("active");
		node1.setAttribute("role","progressbar");
		node1.setAttribute("aria-valuenow","40");
		node1.setAttribute("aria-valuemin","0");
		node1.setAttribute("aria-valuemax","100");
		node1.setAttribute("style","width: "+(0.9*100)+"%");
		node1.setAttribute("id","sentence"+1);
		node1.setAttribute("onclick","clickFunction(sentence"+1+")");
		node1.setAttribute("value","John loves Mia");

		var content = document.createTextNode("<YOUR_CONTENT>");
		node1.appendChild(content);

		node.appendChild(node1);
		document.getElementById("thelist").appendChild(node);
	});
*/

	$("#closeButton").click(function() {
		toggleOverlay();
		$("#nlQuery").show();
		$("#catQuery").show();
		return false;
	});

	/*	$('ul#thelist li').click(function() {
//		$(this).removeClass("deselected").addClass("selected");
		var text = $(this).text();

		var triggerBttn = (this),
		overlay = document.querySelector( 'div.overlay' ),
		closeBttn = overlay.querySelector( 'button.overlay-close' );
		transEndEventNames = {
				'WebkitTransition': 'webkitTransitionEnd',
				'MozTransition': 'transitionend',
				'OTransition': 'oTransitionEnd',
				'msTransition': 'MSTransitionEnd',
				'transition': 'transitionend'
		},
		transEndEventName = transEndEventNames[ Modernizr.prefixed( 'transition' ) ],
		support = { transitions : Modernizr.csstransitions };
		toggleOverlay();	
		return false;
	});*/

	return false;
});

function clickFunction(obj){

	var text = obj.innerText;

	if(globalData==null){
		$('div#information').show();
	}else{
		var dataParsed = JSON.parse(globalData);
		var actualGraph = null;
		dataParsed.forEach(function(e1) {
			if(e1.sent==text){
				actualGraph = e1.data;
			}
		});

		var nodes = {};
		var edges = [];

//		var parsedGraph = JSON.parse(actualGraph);
		actualGraph.forEach(function(e) {
			populate(e, nodes, edges);
		});

//		renderJSObjsToD3(nodes, edges, ".main-svg1");
		renderJSObjsToD3(nodes, edges, ".main-svg");

		var triggerBttn = (this),
		overlay = document.querySelector( 'div.overlay' ),
		closeBttn = overlay.querySelector( 'button.overlay-close' );
		transEndEventNames = {
				'WebkitTransition': 'webkitTransitionEnd',
				'MozTransition': 'transitionend',
				'OTransition': 'oTransitionEnd',
				'msTransition': 'MSTransitionEnd',
				'transition': 'transitionend'
		},
		transEndEventName = transEndEventNames[ Modernizr.prefixed( 'transition' ) ],
		support = { transitions : Modernizr.csstransitions };
		toggleOverlay();
	}
}

function toggleOverlay() {
	$("#nlQuery").hide();
	$("#catQuery").hide();
	overlay = document.querySelector( 'div.overlay' );
	if( classie.has( overlay, 'open' ) ) {
		classie.remove( overlay, 'open' );
		classie.add( overlay, 'close' );
		var onEndTransitionFn = function( ev ) {
			if( support.transitions ) {
				if( ev.propertyName !== 'visibility' ) return;
				this.removeEventListener( transEndEventName, onEndTransitionFn );
			}
			classie.remove( overlay, 'close' );
		};
		if( support.transitions ) {
			overlay.addEventListener( transEndEventName, onEndTransitionFn );
		}
		else {
			onEndTransitionFn();
		}
	}
	else if( !classie.has( overlay, 'close' ) ) {
		classie.add( overlay, 'open' );
	}
}

function submitText(event1,event1Slot,relation,event2,event2Slot,nlQuery) {
	$.ajax({
		type : 'GET',
		dataType : 'html',
		url : "InputOutputServlet",
		data : {
			event1 : event1,
			event1Slot : event1Slot,
			relation : relation,
			event2 : event2,
			event2Slot : event2Slot,
			nlQuery : nlQuery
		},
		success : function(data) {
			$('#information').hide();
			$('div#noKnowledgeInfo').hide();
			$('div#outputKnowledge').hide();
			var parent = document.getElementById("thelist");
			if(parent!=null){
				parent.innerHTML="";
			}
			
			if (data == "null") {
				$('div#information').show();
			} else if (data == "[]") {
				$('div#noKnowledgeInfo').show();
			} else if (data == "Please enter a valid query!") {
				$('div#information').show();
			}else if (data == "An Exception Occured in retrieval.") {
				$('div#information').show();
			}else {
				globalData = data;
				var dataParsed = JSON.parse(data);
//				alert(dataParsed);
				if(dataParsed.length==1){
					$('div#noKnowledgeInfo').show();
				}else{
					
				
				var info = dataParsed.splice(0,1);
				
				$('#event1ValueKnowledge').val(event1+","+info[0].event1List);
				$('#relationValueKnowledge').val(relation+","+info[0].relationsList);
				$('#event2ValueKnowledge').val(event2+","+info[0].event2List);
				$('#event1SlotValueKnowledge').val(event1Slot+","+info[0].slot1List);
				$('#event2SlotValueKnowledge').val(event2Slot+","+info[0].slot2List);
				$('div#outputKnowledge').show();
				
				var parent = document.getElementById("thelist");
				if(parent!=null){
					parent.innerHTML="";
				}
				
				var i = 1;
				dataParsed.forEach(function(e) {
					var node=document.createElement("li");
					node.setAttribute("class","progress");
					node.classList.add("progressBar-modified");
					node.classList.add("deselected");
					
					var node1=document.createElement("div");
					node1.setAttribute("class","progress-bar");
					node1.classList.add("progress-bar-striped");
					node1.classList.add("active");
					node1.setAttribute("role","progressbar");
					node1.setAttribute("aria-valuenow","40");
					node1.setAttribute("aria-valuemin","0");
					node1.setAttribute("aria-valuemax","100");
					node1.setAttribute("style","width: "+(e.weight*100)+"%");
					node1.setAttribute("id","sentence"+i);

					var content = document.createTextNode(e.sent);
					node1.appendChild(content);
					
					node.appendChild(node1);
					document.getElementById("thelist").appendChild(node);
					i++;
				});
//				var nodes = {};
//				var edges = [];

//				dataParsed.forEach(function(e) {
//				populate(e, nodes, edges);
//				});

//				renderJSObjsToD3(nodes, edges, ".main-svg1");
//				renderJSObjsToD3(nodes, edges, ".main-svg");
				}
			}
		},
		complete : function() {
			$('body').css('cursor', 'default');
		}
	});
}

function output(inp) {
	document.body.appendChild(document.createElement('pre')).innerHTML = inp;
}

function populate(data, nodes, edges) {
	var nodeID = Object.keys(nodes).length;

	var newNode = {};
//	var initLabel = "node";
	var initLabel = data.data.word;
//	if(data.data.pos === "VB" && !data.data.isClass){
//		initLabel += " (Tense:present)";
//	}else if(data.data.pos === "VBD" && !data.data.isClass){
//		initLabel += " (Tense:past)";
//	}else if(data.data.pos === "VBG" && !data.data.isClass){
//		initLabel += " (Tense:present participle)";
//	}else if(data.data.pos === "VBN" && !data.data.isClass){
//		initLabel += " (Tense:past participle)";
//	}else if(data.data.pos === "VBP" && !data.data.isClass){
//		initLabel += " (Tense:present)";
//	}else if(data.data.pos === "VBZ" && !data.data.isClass){
//		initLabel += " (Tense:present)";
//	}
	if (data.data.wordSense === undefined) {
		newNode = {
			label : initLabel ,//(data.data.type == "TK") ? data.data.word : data.data.type,
			pos : data.data.pos,
			id : nodeID + "",
			isClass : data.data.isClass,
			isEvent : data.data.isEvent,
			isEntity : data.data.isEntity,
			isASemanticRole : data.data.isASemanticRole,
			isACoreferent : data.data.isACoreferent
		};
	} else {
		newNode = {
			label : initLabel,// +' WS:'+ data.data.wordSense,//(data.data.type == "TK") ? data.data.word : data.data.type,
			pos : data.data.pos,
			id : nodeID + "",
			isClass : data.data.isClass,
			isEvent : data.data.isEvent,
			isEntity : data.data.isEntity,
			isASemanticRole : data.data.isASemanticRole,
			isACoreferent : data.data.isACoreferent
		};
	}

	if (data.data.wordSense) {
		newNode.wordSense = data.data.wordSense;
	}

	if (data.data.Edge) {
		newNode.edge = data.data.Edge;
	}

	/* var classes = [ "type-" + data.data.Edge ];
	if (data.data.pos) {
		classes.push("ne-" + data.data.ne);
	}

	newNode.nodeclass = classes.join(" ");
	 */
	//  I hate javascript
	nodes[nodeID] = newNode;

	data.children.forEach(function(child) {
		var newChild = populate(child, nodes, edges);

		edges.push({
			source : newNode.id,
			target : newChild.id,
			id : newNode.id + "-" + newChild.id,
			label : newChild.edge
		});
	});

	return newNode;
}
//function populate(data, nodes, edges) {
//	var nodeID = Object.keys(nodes).length;
//
//	var newNode = {};
//	if (data.wordSense === undefined) {
//		newNode = {
//				label : data.word,//(data.data.type == "TK") ? data.data.word : data.data.type,
//				pos : data.pos,
//				id : nodeID + "",
//				isClass : data.isClass,
//				isEvent : data.isEvent,
//				isEntity : data.isEntity,
//				isASemanticRole : data.isASemanticRole,
//				isACoreferent : data.isACoreferent
//		};
//	} else {
//		newNode = {
//				label : data.word,// +' WS:'+ data.data.wordSense,//(data.data.type == "TK") ? data.data.word : data.data.type,
//				pos : data.pos,
//				id : nodeID + "",
//				isClass : data.isClass,
//				isEvent : data.isEvent,
//				isEntity : data.isEntity,
//				isASemanticRole : data.isASemanticRole,
//				isACoreferent : data.isACoreferent
//		};
//	}
//
//	if (data.wordSense) {
//		newNode.wordSense = data.wordSense;
//	}
//
//	if (data.Edge) {
//		newNode.edge = data.Edge;
//	}
//
//	/* var classes = [ "type-" + data.data.Edge ];
//			if (data.data.pos) {
//				classes.push("ne-" + data.data.ne);
//			}
//
//			newNode.nodeclass = classes.join(" ");
//	 */
//	//  I hate javascript
//	nodes[nodeID] = newNode;
//
//	data.children.forEach(function(child) {
//		var newChild = populate(child, nodes, edges);
//
//		edges.push({
//			source : newNode.id,
//			target : newChild.id,
//			id : newNode.id + "-" + newChild.id,
//			label : newChild.edge
//		});
//	});
//
//	return newNode;
//}

function buildGraphData(node, nodes, links) {

	var index = nodes.length;
	nodes.push({
		name : node.data.content,
		group : 1
	});

	node.children.forEach(function(e) {
		links.push({
			source : index,
			target : nodes.length,
			value : 2
		});
		buildGraphData(e, nodes, links);
	});
}
