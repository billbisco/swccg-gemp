package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.PlayerLock;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.collection.TransferDAO;
import com.gempukku.swccgo.db.DeckDAO;
import com.gempukku.swccgo.db.GempSettingDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.packagedProduct.ProductName;
import com.gempukku.swccgo.service.LoggedUserHolder;
import com.gempukku.swccgo.common.ApplicationConfiguration;
import com.gempukku.swccgo.service.AuthService;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieHeaderNames;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.*;


public class SwccgoServerRequestHandler {
    public static final int WEEKLY_GOLD = 1500;
    /** New-player starting currency: 10 gold + 10 silver (amount = gold*100 + silver). */
    public static final int SIGNUP_CURRENCY = 1010;
    protected PlayerDAO _playerDao;
    protected LoggedUserHolder _loggedUserHolder;
    private final TransferDAO _transferDAO;
    protected final CollectionsManager _collectionManager;
    protected DeckDAO _deckDao;
    protected GempSettingDAO _gempSettingDAO;
    protected AuthService _authService;

    public SwccgoServerRequestHandler(Map<Type, Object> context) {
        _playerDao = extractObject(context, PlayerDAO.class);
        _loggedUserHolder = extractObject(context, LoggedUserHolder.class);
        _transferDAO = extractObject(context, TransferDAO.class);
        _collectionManager = extractObject(context, CollectionsManager.class);
        _deckDao = extractObject(context, DeckDAO.class);
        _gempSettingDAO = extractObject(context, GempSettingDAO.class);
        _authService = extractObject(context, AuthService.class);
    }

    private boolean isTest() {
        return Boolean.parseBoolean(System.getProperty("test"));
    }

    protected final void processLoginReward(String loggedUser) throws Exception {
        if (loggedUser != null) {
            Player player = _playerDao.getPlayer(loggedUser);
            synchronized (PlayerLock.getLock(player)) {
                int currentDate = DateUtils.getCurrentDate();
                int latestMonday = DateUtils.getMondayBeforeOrOn(currentDate);

                Integer lastReward = player.getLastLoginReward();
                if (lastReward == null) {
                    if (_playerDao.updateLastReward(player, null, latestMonday)) {

                        // Add initial signup reward: 10 gold + 10 silver, plus Jedi Pack and Rebel Leader pack.
                        // Only runs when lastLoginReward is null (brand-new players), not on every login.
                        _collectionManager.addCurrencyToPlayerCollection(true, "Signup reward", player, CollectionType.MY_CARDS, SIGNUP_CURRENCY);
                        _collectionManager.addItemsToPlayerCollection(true, "Free Jedi Pack and Rebel Leader Pack", player, CollectionType.MY_CARDS,
                                Arrays.asList(CardCollection.Item.createItem(ProductName.JEDI_PACK, 1), CardCollection.Item.createItem(ProductName.REBEL_LEADER_PACK, 1)));
                    }
                }
                else {
                    if (latestMonday != lastReward) {
                        if (_playerDao.updateLastReward(player, lastReward, latestMonday)) {
                            _collectionManager.addCurrencyToPlayerCollection(true, "Weekly reward", player, CollectionType.MY_CARDS, WEEKLY_GOLD);
                        }
                    }
                }
            }
        }
    }

    /** Value of the named request cookie, or null. */
    protected static String getCookieValue(HttpRequest request, String name) {
        String cookieHeader = request.headers().get(HttpHeaderNames.COOKIE);
        if (cookieHeader == null)
            return null;
        for (Cookie cookie : ServerCookieDecoder.STRICT.decode(cookieHeader)) {
            if (cookie.name().equals(name))
                return cookie.value();
        }
        return null;
    }

    /**
     * Whether cookies get the Secure flag. Production is HTTPS-only behind nginx; browsers also accept
     * Secure cookies on http://localhost, so the default (true) works for local dev too.
     * Set {@code session.cookie.secure=false} only for a plain-HTTP non-localhost test host.
     */
    static boolean secureCookies() {
        String value = ApplicationConfiguration.getProperty("session.cookie.secure");
        return value == null || !value.trim().equalsIgnoreCase("false");
    }

    /**
     * Encodes a cookie with HttpOnly, SameSite and (by default) Secure set.
     * @param maxAgeSeconds max age, or Long.MIN_VALUE for a browser-session cookie; 0 deletes it
     */
    protected static String encodeProtectedCookie(String name, String value, long maxAgeSeconds, CookieHeaderNames.SameSite sameSite) {
        DefaultCookie cookie = new DefaultCookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookies());
        cookie.setSameSite(sameSite);
        cookie.setPath("/");
        if (maxAgeSeconds != Long.MIN_VALUE)
            cookie.setMaxAge(maxAgeSeconds);
        return ServerCookieEncoder.STRICT.encode(cookie);
    }

    private String getLoggedUser(HttpRequest request) {
        ServerCookieDecoder cookieDecoder = ServerCookieDecoder.STRICT;
        String cookieHeader = request.headers().get(HttpHeaderNames.COOKIE);
        if (cookieHeader != null) {
            Set<Cookie> cookies = cookieDecoder.decode(cookieHeader);
            for (Cookie cookie : cookies) {
                if (cookie.name().equals("loggedUser")) {
                    String value = cookie.value();
                    if (value != null) {
                        return _loggedUserHolder.getLoggedUser(value);
                    }
                }
            }
        }
        return null;
    }

    protected final void processDeliveryServiceNotification(HttpRequest request, Map<String, String> headersToAdd) {
        processDeliveryServiceNotificationForPlayer(getLoggedUser(request), headersToAdd);
    }

    protected final void processDeliveryServiceNotificationForPlayer(String playerName, Map<String, String> headersToAdd) {
        if (playerName != null && _transferDAO.hasUndeliveredPackages(playerName))
            headersToAdd.put("Delivery-Service-Package", "true");
    }

    protected final Player getResourceOwnerSafely(HttpRequest request, String participantId) throws HttpProcessingException {
        String loggedUser = getLoggedUser(request);
        if (isTest() && loggedUser == null)
            loggedUser = participantId;

        if (loggedUser == null)
            throw new HttpProcessingException(401);

        Player resourceOwner = _playerDao.getPlayer(loggedUser);

        if (resourceOwner == null)
            throw new HttpProcessingException(401);

        if (resourceOwner.getType().contains("a") && participantId != null && !participantId.equals("null") && !participantId.equals("")) {
            resourceOwner = _playerDao.getPlayer(participantId);
            if (resourceOwner == null)
                throw new HttpProcessingException(401);
        }
        return resourceOwner;
    }

    protected final Player getLibrarian() throws HttpProcessingException {
        Player resourceOwner = _playerDao.getPlayer("Librarian");

        if (resourceOwner == null)
            throw new HttpProcessingException(401);

        return resourceOwner;
    }

    protected String getQueryParameterSafely(QueryStringDecoder queryStringDecoder, String parameterName) {
        List<String> parameterValues = queryStringDecoder.parameters().get(parameterName);
        if (parameterValues != null && !parameterValues.isEmpty())
            return parameterValues.get(0);
        else
            return null;
    }

    protected List<String> getFormMultipleParametersSafely(HttpPostRequestDecoder postRequestDecoder, String parameterName) throws HttpPostRequestDecoder.NotEnoughDataDecoderException, IOException {
        List<String> result = new LinkedList<String>();
        List<InterfaceHttpData> datas = postRequestDecoder.getBodyHttpDatas(parameterName);
        if (datas == null)
            return Collections.emptyList();
        for (InterfaceHttpData data : datas) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                Attribute attribute = (Attribute) data;
                result.add(attribute.getValue());
            }

        }
        return result;
    }

    protected String getFormParameterSafely(HttpPostRequestDecoder postRequestDecoder, String parameterName) throws IOException, HttpPostRequestDecoder.NotEnoughDataDecoderException {
        InterfaceHttpData data = postRequestDecoder.getBodyHttpData(parameterName);
        if (data == null)
            return null;
        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
            Attribute attribute = (Attribute) data;
            return attribute.getValue();
        } else {
            return null;
        }
    }

    protected List<String> getFormParametersSafely(HttpPostRequestDecoder postRequestDecoder, String parameterName) throws IOException, HttpPostRequestDecoder.NotEnoughDataDecoderException {
        List<InterfaceHttpData> datas = postRequestDecoder.getBodyHttpDatas(parameterName);
        if (datas == null)
            return null;
        List<String> result = new LinkedList<String>();
        for (InterfaceHttpData data : datas) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                Attribute attribute = (Attribute) data;
                result.add(attribute.getValue());
            }
        }
        return result;
    }

    protected <T> T extractObject(Map<Type, Object> context, Class<T> clazz) {
        Object value = context.get(clazz);
        return clazz.cast(value);
    }

    protected Map<String, String> logUserReturningHeaders(String remoteIp, String login) throws SQLException {
        _playerDao.updateLastLoginIp(login, remoteIp);

        String sessionId = _loggedUserHolder.logUser(login);
        // HttpOnly: page scripts (and any injected script) cannot read the session.
        // SameSite=Lax: other sites cannot make the browser send it on POSTs (CSRF).
        // Secure: HTTPS only (see secureCookies()).
        return Collections.singletonMap(
                HttpHeaderNames.SET_COOKIE.toString(),
                encodeProtectedCookie("loggedUser", sessionId, Long.MIN_VALUE, CookieHeaderNames.SameSite.Lax));
    }
}
