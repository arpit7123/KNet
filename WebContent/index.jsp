<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
 
<title>The Commonsense Knowledge Database</title>
 
<!-- Bootstrap Core CSS -->
<link href="css/bootstrap.min.css" rel="stylesheet">
 
<!-- Custom CSS -->
<link href="css/agency.css" rel="stylesheet">
<link href="css/style.css" rel="stylesheet">
 
<!-- css for overlay -->
<link href="css/style1.css" rel="stylesheet">
 
<!-- css for d3 -->
<link rel="stylesheet" type="text/css" href="css/dagre-d3-simple.css">
 
<!-- Custom Fonts -->
<link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
<link href="http://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
<link href='http://fonts.googleapis.com/css?family=Kaushan+Script' rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Droid+Serif:400,700,400italic,700italic' rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Roboto+Slab:400,100,300,700' rel='stylesheet' type='text/css'>
<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
 
</head>
 
<body id="page-top" class="index">
 
    <!-- Navigation -->
    <nav class="navbar navbar-default navbar-fixed-top">
        <div class="container">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header page-scroll">
                <button type="button" class="navbar-toggle" data-toggle="collapse"
                    data-target="#bs-example-navbar-collapse-1">
                    <span class="sr-only">Toggle navigation</span> <span
                        class="icon-bar"></span> <span class="icon-bar"></span> <span
                        class="icon-bar"></span>
                </button>
                <a class="navbar-brand page-scroll" href="#page-top">The
                    Commonsense Knowledge Database</a>
            </div>
 
            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse"
                id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav navbar-right">
                    <li class="hidden"><a href="#page-top"></a></li>
                    <li><a class="page-scroll" href="#about">About</a></li>
                    <li><a class="page-scroll" href="#services">Demo</a></li>
                </ul>
            </div>
        </div>
    </nav>
    
    
        <!-- Header -->
    <header>
        <div class="container">
            <div class="intro-text">
                <div class="intro-lead-in modified">This is a demo Website!</div>
                <div class="intro-heading modified"></div>
                <a href="#services" class="page-scroll btn btn-xl">Take me to the demo</a>
            </div>
        </div>
    </header>
 
    <!-- About Section -->
    <section id="about">
        <div class="container container-new">
            <div class="row">
                <div class="col-lg-12">
                    <h2 class="section-heading">About Events-based Conditional Commonsense Knowledge Database</h2>
                    <!-- <h3 class="section-subheading text-muted">Lorem ipsum dolor sit amet consectetur.</h3> -->
                    <p>ECCK database is a commonsense knowledge base containing a new kind of
                        commonsense knowledge. This knowledge is useful in simulating
                        human-like intelligence in computers and hence making them solve a
                        number of Natural Language Understanding problems such as hard
                        co-reference resolution and reading comprehension. The example
                        below explains the kind of commonsense knowledge
                        present in this knowledge base.</P>
                    <p>
                        Let us consider an NLU example that represents both deep question
                        answering and hard co-reference resolution. Here, we are given a
                        sentence and a question based on the sentence. The answer to the question depends
                        on the resolution a pronoun in the sentence to its corefernt
                        (which is also present in the sentence). The sentence is created
                        in such a way that if a word in the sentence is replaced by
                        another word, then the resolution of the pronoun i.e. the answer
                        of the question also changes (as shown in the example 2 below).
                        This example is inspired from <a
                            href="http://www.cs.nyu.edu/davise/papers/WS.html">The
                            Winograd Schema Challenge</a>
                        </p>
                    <p>
                        <b>EXAMPLE 1:-</b><br> <b>Sentence:</b> John was bullying Tom
                        so we <i>rescued</i> him.<br> <b>Question:</b> Who did we
                        rescue ?<br> <b>Answer:</b> Tom<br>
                    </p>
                    <p>
                        <b>EXAMPLE 1:-</b><br> <b>Sentence:</b> John was bullying Tom
                        so we <i>punish</i> him.<br> <b>Question:</b> Who did we
                        punish ?<br> <b>Answer:</b> John
                    </p>
                    <p>
                        The commonsense knowledge required to answer the question in <b>Example1</b>
                        is: <br> <b>IF</b> A <i>bullying</i> B <i>causes</i> T <i>rescued</i>
                        Z <b>THEN possibly</b> Z = B <br> It is explained with the help of the
                        picture below.
                    </p>
                    <img src="./img/test.png" alt="commonsense knowledge">
                    <p>
                        In Other Words; <br> <b>If</b> bully event <b>causes</b>
                        rescue event <br> <b>then, possibly</b> <i>recipient</i> of "bully" event
                        = <i>recipient</i> of "rescued" event
                    </p>
                    <p>
                        Our ECCK database contains above mentioned kind of commonsense knowledge.
                        In this website we present a <a class="page-scroll" href="#services">demo</a> of our database.
                    </p>
                </div>
            </div>
        </div>
    </section>
        <!-- Services Section -->
    <section id="services">
        <div class="container container-new">
            <div class="row">
                <div class="col-lg-12">
                    <h2 class="section-heading">Demo</h2>
                    <div class="section-subheading text-muted">
                    <p>
                    The database supports seven different types of queries based on the commonsense mentioned
                    above. The queries are:-
                    </p>
                    <ul>
                        <li>Event1=E1,Rel=R,Event2=E2,Slot1=S1,Slot2=S2</li>
                        <p>Example:- Event1=bully,Rel=causes,Event2=rescue,Slot1=recipient,Slot2=recipient</p>
                        <li>Event1=*,Rel=R,Event2=E2,Slot1=S1,Slot2=S2</li>
                        <p>Example:- Event1=*,Rel=causes,Event2=rescue,Slot1=recipient,Slot2=recipient</p>
                        <li>Event1=E1,Rel=R,Event2=*,Slot1=S1,Slot2=S2</li>
                        <p>Example:- Event1=bully,Rel=causes,Event2=*,Slot1=recipient,Slot2=recipient</p>
                        <li>Event1=E1,Rel=R,Event2=E2,Slot1=*,Slot2=S2</li>
                        <p>Example:- Event1=bully,Rel=causes,Event2=rescue,Slot1=*,Slot2=recipient</p>
                        <li>Event1=E1,Rel=R,Event2=E2,Slot1=S1,Slot2=*</li>
                        <p>Example:- Event1=bully,Rel=causes,Event2=rescue,Slot1=recipient,Slot2=*</p>
                        <li>Event1=E1,Rel=*,Event2=E2,Slot1=S1,Slot2=S2</li>
                        <p>Example:- Event1=bully,Rel=*,Event2=rescue,Slot1=recipient,Slot2=recipient</p>
                        <li>Event1=E1,Rel=*,Event2=E2,Slot1=*,Slot2=*</li>
                        <p>Example:- Event1=bully,Rel=*,Event2=rescue,Slot1=*,Slot2=*</p>
                    </ul>
                    </div>
                    <br> 
                     <div>
                     <h4>Following five relations could be used in the queries:</h4>
                     <ul>
                     <li><h5>objective</h5>: If the objective of one event is another event
                     <li><h5>previous_event:</h5> If event1 has event2 as previously occurred event
                     <li><h5>next_event:</h5> If event1 has event2 as next occurred event
                     <li><h5>caused_by:</h5> If event1 is caused by the occurrence of event2
                     <li><h5>causes:</h5> If event1 causes event2.
                     </ul>
                     </div>
                    <br>
                        
                    <h3 class="section-subheading text-muted">Please enter your
                        query (only from the examples presented above) in the fields below.</h3>
                </div>
            </div>
    
                <div class="col-lg-10">
                <form role="form" id="input-form">
                    <div class="row">
                        <div class="col-lg-10">
                            <div class="input-group" id="nlQuery">
                                <textarea id="inputQuery" class="form-control" rows="1"
                                    cols="70" placeholder="Enter your query here"></textarea>
                            </div>
                        </div>
                    </div>
                    <!-- <br> -->
                    <h3>OR</h3>
                    <!-- <br> -->
                    <div class="row" id="catQuery">
                        <div class="col-lg-4">
                            <div class="input-group">
                                <span class="input-group-addon" id="event1">Event1</span> <input
                                     id="event1Value" type="text" class="form-control" aria-label="...">
                            </div>
                            <div class="input-group">
                                <span class="input-group-addon" id="event1Slot">Event1
                                    Slot</span> <input id="event1SlotValue" type="text" class="form-control" aria-label="...">
                            </div>
                            <!-- /input-group -->
                        </div>
                        <!-- /.col-lg-2 -->
                        <div class="col-lg-4">
                            <div class="input-group">
                                <span class="input-group-addon" id="relation">Relation</span> <input
                                    id="relationValue" type="text" class="form-control" aria-label="...">
                            </div>
                            <!-- /input-group -->
                        </div>
                        <!-- /.col-lg-2 -->
                        <div class="col-lg-4">
                            <div class="input-group">
                                <span class="input-group-addon" id="event2">Event2</span> <input
                                id="event2Value" type="text" class="form-control" aria-label="...">
							</div>
                            <div class="input-group">
                                <span class="input-group-addon" id="event2Slot">Event2
                                    Slot</span> <input id="event2SlotValue" type="text" class="form-control" aria-label="...">
                            </div>
                            <!-- /input-group -->
                        </div>
                        <!-- /.col-lg-2 -->
                    </div>
                    <!-- /.row -->


                    <br>
                    <div class="row" align="center">
                        <button type="reset" class="btn btn-default" id="Clear">Clear</button>
                        <button type="submit" class="btn btn-primary" id="submit" name=""
                            value="Submit">Submit</button>
                    </div>
                </form>
                <br>
                
                <!-- OUTPUT KNOWLEDGE BLOCK BEGIN  -->
                <div class="row" id="outputKnowledge" style="display: none">
                <span class="input-group-addon" id="event1">OUTPUT KNOWLEDGE</span>
                <br>
                        <div class="col-lg-4">
                            <div class="input-group">
                                <span class="input-group-addon" id="event1Knowledge">Event1</span> <input
                                     id="event1ValueKnowledge" type="text" class="form-control" aria-label="..." disabled = "disabled">
                            </div>
                            <div class="input-group">
                                <span class="input-group-addon" id="event1SlotKnowledge">Event1
                                    Slot</span> <input id="event1SlotValueKnowledge" type="text" class="form-control" aria-label="..." disabled = "disabled">
                            </div>
                            <!-- /input-group -->
                        </div>
                        <!-- /.col-lg-2 -->
                        <div class="col-lg-4">
                            <div class="input-group">
                                <span class="input-group-addon" id="relationKnowledge">Relation</span> <input
                                    id="relationValueKnowledge" type="text" class="form-control" aria-label="..." disabled = "disabled">
                            </div>
                            <!-- /input-group -->
                        </div>
                        <!-- /.col-lg-2 -->
                        <div class="col-lg-4">
                            <div class="input-group">
                                <span class="input-group-addon" id="event2Knowledge">Event2</span> <input
                                id="event2ValueKnowledge" type="text" class="form-control" aria-label="..." disabled = "disabled">
							</div>
                            <div class="input-group">
                                <span class="input-group-addon" id="event2SlotKnowledge">Event2
                                    Slot</span> <input id="event2SlotValueKnowledge" type="text" class="form-control" aria-label="..." disabled = "disabled">
                            </div>
                            <!-- /input-group -->
                        </div>
                        <!-- /.col-lg-2 -->
                    </div>
                    <!-- /.row -->
                 <!-- OUTPUT KNOWLEDGE BLOCK END -->   
                    
                    <br>
                <div id="information" class="alert alert-danger" role="alert"
                    style="display: none">
                    <strong>Error!</strong> Please enter a valid Query.
                </div>
                <div id="noKnowledgeInfo" class="alert alert-info" role="alert"
                    style="display: none">
                    No knowledge found !
                </div>
                <br>
                
                <ul class="container" id="thelist">
                </ul>

                <div class="overlay overlay-hugeinc" id="attach">
                    <button type="button" class="overlay-close" id="closeButton"
                        title="close">Close</button>
                    <svg class="main-svg" id="svg-canvas"></svg>
                </div>
            </div>
        </div>
    </section>
 
 
    <!-- jQuery -->
    <script src="js/jquery.js"></script>
 
    <!-- Bootstrap Core JavaScript -->
    <script src="js/bootstrap.min.js"></script>
 
    <!-- Plugin JavaScript -->
    <script
        src="http://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.3/jquery.easing.min.js"></script>
    <script src="js/classie.js"></script>
    <script src="js/cbpAnimatedHeader.js"></script>
 
    <!-- Contact Form JavaScript -->
    <script src="js/jqBootstrapValidation.js"></script>
    <script src="js/contact_me.js"></script>
 
    <!-- Custom Theme JavaScript -->
    <script src="js/agency.js"></script>
 
    <!-- script for main functionality -->
    <script src="js/main.js"></script>
 
    <!-- script for overlay -->
    <script src="js/modernizr.custom.js"></script>
 
    <!-- D3 scripts -->
    <script type="text/javascript" src="resources/d3.min.js"></script>
    <script type="text/javascript" src="resources/dagre.min.js"></script>
    <script type="text/javascript" src="resources/dagre-d3-simple.js"></script>

</body>

</html>


<!-- <!DOCTYPE html>
<html lang="en">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title>The Commonsense Knowledge Database</title>

Bootstrap Core CSS
<link href="css/bootstrap.min.css" rel="stylesheet">

Custom CSS
<link href="css/agency.css" rel="stylesheet">
<link href="css/style.css" rel="stylesheet">

css for overlay
<link href="css/style1.css" rel="stylesheet">

css for d3
<link rel="stylesheet" type="text/css" href="css/dagre-d3-simple.css">

Custom Fonts
<link href="font-awesome/css/font-awesome.min.css" rel="stylesheet"	type="text/css">
<link href="http://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
<link href='http://fonts.googleapis.com/css?family=Kaushan+Script' rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Droid+Serif:400,700,400italic,700italic'	rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Roboto+Slab:400,100,300,700' rel='stylesheet' type='text/css'>

HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries
WARNING: Respond.js doesn't work if you view the page via file://
[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]

</head>

<body id="page-top" class="index">

	Navigation
	<nav class="navbar navbar-default navbar-fixed-top">
		<div class="container">
			Brand and toggle get grouped for better mobile display
			<div class="navbar-header page-scroll">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target="#bs-example-navbar-collapse-1">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand page-scroll" href="#page-top">The
					Commonsense Knowledge Database</a>
			</div>

			Collect the nav links, forms, and other content for toggling
			<div class="collapse navbar-collapse"
				id="bs-example-navbar-collapse-1">
				<ul class="nav navbar-nav navbar-right">
					<li class="hidden"><a href="#page-top"></a></li>
					<li><a class="page-scroll" href="#about">About</a></li>
					<li><a class="page-scroll" href="#services">Demo</a></li>
				</ul>
			</div>
		</div>
	</nav>

	Header
	<header>
		<div class="container">
			<div class="intro-text">
				<div class="intro-lead-in modified">This is a demo Website!</div>
				<div class="intro-heading modified"></div>
				<a href="#services" class="page-scroll btn btn-xl">Take me to the demo</a>
			</div>
		</div>
	</header>

	About Section
	<section id="about">
		<div class="container container-new">
			<div class="row">
				<div class="col-lg-12">
					<h2 class="section-heading">About Events-based Conditional Commonsense Knowledge Database</h2>
					<h3 class="section-subheading text-muted">Lorem ipsum dolor sit amet consectetur.</h3>
					<p>ECCK database is a commonsense knowledge base containing a new kind of
						commonsense knowledge. This knowledge is useful in simulating
						human-like intelligence in computers and hence making them solve a
						number of Natural Language Understanding problems such as hard
						co-reference resolution and reading comprehension. The example
						below explains the kind of commonsense knowledge
						present in this knowledge base.</P>
					<p>
						Let us consider an NLU example that represents both deep question
						answering and hard co-reference resolution. Here, we are given a
						sentence and a question based on the sentence. The answer to the question depends
						on the resolution a pronoun in the sentence to its corefernt
						(which is also present in the sentence). The sentence is created
						in such a way that if a word in the sentence is replaced by
						another word, then the resolution of the pronoun i.e. the answer
						of the question also changes (as shown in the example 2 below).
						This example is inspired from <a
							href="http://www.cs.nyu.edu/davise/papers/WS.html">The
							Winograd Schema Challenge</a>
					</p>
					<p>
						<b>EXAMPLE 1:-</b><br> <b>Sentence:</b> John was bullying Tom
						so we <i>rescued</i> him.<br> <b>Question:</b> Who did we
						rescue ?<br> <b>Answer:</b> Tom<br>
					</p>
					<p>
						<b>EXAMPLE 1:-</b><br> <b>Sentence:</b> John was bullying Tom
						so we <i>punish</i> him.<br> <b>Question:</b> Who did we
						punish ?<br> <b>Answer:</b> John
					</p>
					<p>
						The commonsense knowledge required to answer the question in <b>Example1</b>
						is: <br> <b>IF</b> A <i>bullying</i> B <i>causes</i> T <i>rescued</i>
						Z <b>THEN possibly</b> Z = B <br> It is explained with the help of the
						picture below.
					</p>
					<img src="./img/test.png" alt="commonsense knowledge">
					<p>
						In Other Words; <br> <b>If</b> bully event <b>causes</b>
						rescue event <br> <b>then, possibly</b> <i>recipient</i> of "bully" event
						= <i>recipient</i> of "rescued" event
					</p>
					<p>
						Our ECCK database contains above mentioned kind of commonsense knowledge.
						In this website we present a <a class="page-scroll" href="#services">demo</a> of our database.
					</p>
				</div>
			</div>
		</div>
	</section>

	Services Section
	<section id="services">
		<div class="container container-new">
			<div class="row">
				<div class="col-lg-12">
					<h2 class="section-heading">Demo</h2>
					<div class="section-subheading text-muted">
					<p>
					The database supports seven	different types of queries based on the commonsense mentioned
					above. The queries are:-
					</p>
					<ul>
						<li>Event1=E1, REL=R, Event2=E2, Slot1=S1, Slot2=S2</li>
						<p>Example:- Event1=bully, REL=next_event, Event2=rescue, Slot1=recipient, Slot2=recipient</p>
						<li>Event1=*, REL=R, Event2=E2, Slot1=S1, Slot2=S2</li>
						<p>Example:- Event1=*, REL=next_event, Event2=rescue, Slot1=recipient, Slot2=recipient</p>
						<li>Event1=E1, REL=R, Event2=*, Slot1=S1, Slot2=S2</li>
						<p>Example:- Event1=bully, REL=next_event, Event2=*, Slot1=recipient, Slot2=recipient</p>
						<li>Event1=E1, REL=R, Event2=E2, Slot1=*, Slot2=S2</li>
						<p>Example:- Event1=bully, REL=next_event, Event2=rescue, Slot1=*, Slot2=recipient</p>
						<li>Event1=E1, REL=R, Event2=E2, Slot1=S1, Slot2=*</li>
						<p>Example:- Event1=bully, REL=next_event, Event2=rescue, Slot1=recipient, Slot2=*</p>
						<li>Event1=E1, REL=*, Event2=E2, Slot1=S1, Slot2=S2</li>
						<p>Example:- Event1=bully, REL=*, Event2=rescue, Slot1=recipient, Slot2=recipient</p>
						<li>Event1=E1, REL=*, Event2=E2, Slot1=*, Slot2=*</li>
						<p>Example:- Event1=bully, REL=*, Event2=rescue, Slot1=*, Slot2=*</p>
					</ul>
					</div>
					<br>					
					<h3 class="section-subheading text-muted">Please enter your
						query (only from the examples presented above) in the fields below.</h3>
				</div>
			</div>

			<div class="col-lg-10">
				<form role="form" id="input-form">
					<div class="row">
						<div class="col-lg-10">
							<div class="input-group" id="nlQuery">
								<textarea id="inputQuery" class="form-control" rows="1"
									cols="70" placeholder="Enter your query here"></textarea>
							</div>
						</div>
					</div>
					<br>
					<h3>OR</h3>
					<br>
					<div class="row" id="catQuery">
						<div class="col-lg-4">
							<div class="input-group">
								<span class="input-group-addon" id="event1">Event1</span> <input
									 id="event1Value" type="text" class="form-control" aria-label="...">
							</div>
							<div class="input-group">
								<span class="input-group-addon" id="event1Slot">Event1
									Slot</span> <input id="event1SlotValue" type="text" class="form-control" aria-label="...">
							</div>
							/input-group
						</div>
						/.col-lg-2
						<div class="col-lg-4">
							<div class="input-group">
								<span class="input-group-addon" id="relation">Relation</span> <input
									id="relationValue" type="text" class="form-control" aria-label="...">
							</div>
							/input-group
						</div>
						/.col-lg-2
						<div class="col-lg-4">
							<div class="input-group">
								<span class="input-group-addon" id="event2">Event2</span> <input
									id="event2Value" type="text" class="form-control" aria-label="...">
							</div>
							<div class="input-group">
								<span class="input-group-addon" id="event2Slot">Event2
									Slot</span> <input id="event2SlotValue" type="text" class="form-control" aria-label="...">
							</div>
							/input-group
						</div>
						/.col-lg-2
					</div>
					/.row


					<br>
					<div class="row" align="center">
						<button type="reset" class="btn btn-default" id="Clear">Clear</button>
						<button type="submit" class="btn btn-primary" id="submit" name=""
							value="Submit">Submit</button>
					</div>
				</form>
				<br>
				<div id="information" class="alert alert-danger" role="alert"
					style="display: none">
					<strong>Error!</strong> Please enter a valid Query.
				</div>
				<div id="noKnowledgeInfo" class="alert alert-info" role="alert"
					style="display: none">
					No knowledge found !
				</div>
				<br>
				<ul class="container" id="thelist">
				</ul>

				<div class="overlay overlay-hugeinc" id="attach">
					<button type="button" class="overlay-close" id="closeButton"
						title="close">Close</button>
					<svg class="main-svg" id="svg-canvas"></svg>
				</div>
			</div>
		</div>
	</section>


	jQuery
	<script src="js/jquery.js"></script>

	Bootstrap Core JavaScript
	<script src="js/bootstrap.min.js"></script>

	Plugin JavaScript
	<script
		src="http://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.3/jquery.easing.min.js"></script>
	<script src="js/classie.js"></script>
	<script src="js/cbpAnimatedHeader.js"></script>

	Contact Form JavaScript
	<script src="js/jqBootstrapValidation.js"></script>
	<script src="js/contact_me.js"></script>

	Custom Theme JavaScript
	<script src="js/agency.js"></script>

	script for main functionality
	<script src="js/main.js"></script>

	script for overlay
	<script src="js/modernizr.custom.js"></script>

	D3 scripts
	<script type="text/javascript" src="resources/d3.min.js"></script>
	<script type="text/javascript" src="resources/dagre.min.js"></script>
	<script type="text/javascript" src="resources/dagre-d3-simple.js"></script>

</body>

</html>
 -->