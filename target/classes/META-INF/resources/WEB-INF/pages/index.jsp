<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>Prog.academy</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
          rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
</head>
<body>
    <div align="center">
        <h1>Your login is: ${login}, your roles are: </h1>
        <c:forEach var="s" items="${roles}">
            <h3><c:out value="${s}" /></h3>
        </c:forEach>

        <c:if test="${admin}">
            <c:url value="/admin" var="adminUrl" />
            <p><a href="${adminUrl}">Click</a> for admin page</p>
        </c:if>
        <div class="card" style="width: 25rem;">
            <img src="<c:url value="/img/${photo}"/>" class="card-img-top" alt="${login}'s photo">
            <div class="card-body">
                <form action="/uploadFile/${photo}" method="POST" enctype="multipart/form-data">
                    <div class="input-group mb-3">
                        <input type="file" name="file" class="form-control">
                        <input type="submit" class="input-group-text" value="Update photo"/>
                    </div>
                </form>
                <c:url value="/update" var="updateUrl" />
                <form action="${updateUrl}" method="POST">
                    <br/><input class="form-control" type="text" name="email" value="${email}" placeholder="E-mail"/><br/>
                    <br/><input class="form-control" type="text" name="phone" value="${phone}" placeholder="Phone" /><br/>
                    <br/><input class="form-control" type="text" name="phone" value="${address}" placeholder="Address" /><br/>
                    <input class="input-group-text mt-2 bg-primary text-white" type="submit" value="Update" class="input-group-text"/>
                </form>
                <c:if test="${param.fail ne null}">
                    <p class="text-danger">Update failed! ${fail}</p>
                </c:if>
            </div>
        </div>

        <c:url value="/logout" var="logoutUrl" />
        <p>Click to logout: <a href="${logoutUrl}">LOGOUT</a></p>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p" crossorigin="anonymous"></script>
</body>
</html>
