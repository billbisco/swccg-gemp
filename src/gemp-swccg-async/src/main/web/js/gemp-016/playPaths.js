/*
 * Where Play lives (#267). On swccg.com Play is served under /play/ and its API under
 * /play/gemp-swccg-server; older hosts (and the Test server) still use /gemp-swccg/ and
 * /gemp-swccg-server at the root. Loaded first on every page, before any script that calls the server.
 */
var GEMP_UI_ROOT = (window.location.pathname.indexOf("/play/") === 0) ? "/play/" : "/gemp-swccg/";
var GEMP_SERVER = (window.location.pathname.indexOf("/play/") === 0) ? "/play/gemp-swccg-server" : "/gemp-swccg-server";
