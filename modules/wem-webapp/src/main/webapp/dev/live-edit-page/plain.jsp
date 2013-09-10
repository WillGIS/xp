<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>

<html lang="en">
<head>
  <meta charset="utf-8">

  <title>Plain Page</title>

  <%@ include file="live-edit/css.jsp" %>

</head>

<body data-live-edit-type="0" data-live-edit-key="/path/to/this/page" data-live-edit-name="Jumping Jack - Frogger">

<%@ include file="live-edit/loader-splash.jsp" %>

<div id="main" data-live-edit-type="1" data-live-edit-key="80" data-live-edit-name="Main">

  <div class="row-fluid" data-live-edit-type="2" data-live-edit-key="10016" data-live-edit-name="2 Column Layout">
    <div class="span6" data-live-edit-type="1" data-live-edit-name="Left Column">
      <!-- // -->
      <%@ include file="../../admin2/live-edit/data/mock-component-10011.html" %>

    </div>

    <div class="span6" data-live-edit-type="1" data-live-edit-name="Right Column">
      <!-- // -->
    </div>
  </div>

</div>

<%@ include file="live-edit/scripts.jsp" %>

</body>
</html>