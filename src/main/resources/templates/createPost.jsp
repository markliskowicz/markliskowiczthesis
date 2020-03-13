<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<title>Create Post</title>
</head>
<body>
    <div id="global">
        <form:form commandName="createPost" action="createPost" method="post" enctype="multipart/form-data" target="_blank">
            <fieldset>
                <legend>Create Post</legend>
                
                <p>
                    <label for="body">Body: </label>
                    <form:input id="description" path="description" />
                </p>
                <p>
                    <label for="image">Images: </label> 
                    <input type="file" name="images" multiple="multiple"/>
                </p>
                <p>
                    <label for="isPostToTwitter">Images: </label> 
                    <input type="checkbox" name="isPostToTwitter"/>
                </p>
                <p>
                    <label for="isPostToFacebook">Images: </label> 
                    <input type="checkbox" name="isPostToFacebook"/>
                </p>
                <p>
                    <label for="isPostToInstagram">Images: </label> 
                    <input type="checkbox" name="isPostToInstagram"/>
                </p>
                
                <p id="buttons">
                    <input id="reset" type="reset" tabindex="4"> 
                    <input id="submit" type="submit" tabindex="5" value="Add Product">
                </p>
            </fieldset>
        </form:form>
    </div>
</body>
</html>