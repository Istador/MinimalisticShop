<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE shop SYSTEM "shop.dtd">
<?xml-stylesheet href="shop.xslt" type="text/xsl"?>
<%@ 
	page language="java" contentType="text/xml; charset=UTF-8" pageEncoding="UTF-8" 
	import="static shop.Shop.*"
%>
<% bestellung(request, session, out, response); %>