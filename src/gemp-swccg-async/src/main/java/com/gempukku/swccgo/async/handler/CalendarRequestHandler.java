package com.gempukku.swccgo.async.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.events.CalendarService;
import com.gempukku.swccgo.game.Player;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Map;

/**
 * GET /calendar?from=yyyy-MM-dd&amp;to=yyyy-MM-dd
 * <p>
 * Every event that touches the requested range, as JSON: leagues (with their series) and tournaments.  The client
 * (calendarUi.js) renders the grid.  A missing or malformed date, a range that runs backwards or one longer than
 * {@link CalendarService#MAX_RANGE_DAYS} is a 400 whose {@code message} header says why.  Login is required, as
 * everywhere in the hall.
 */
public class CalendarRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private final CalendarService _calendarService;

    public CalendarRequestHandler(Map<Type, Object> context) {
        super(context);
        _calendarService = extractObject(context, CalendarService.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if ((uri.isEmpty() || uri.equals("/")) && request.method() == HttpMethod.GET) {
            getCalendar(request, responseWriter);
        } else {
            throw new HttpProcessingException(404);
        }
    }

    private void getCalendar(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        Player viewer = getResourceOwnerSafely(request, participantId);

        LocalDate from = CalendarService.parseDate(getQueryParameterSafely(queryDecoder, "from"));
        LocalDate to = CalendarService.parseDate(getQueryParameterSafely(queryDecoder, "to"));
        String problem = CalendarService.validateRange(from, to);
        if (problem != null)
            throw new HttpProcessingException(400, problem);

        Map<String, Object> result = _calendarService.getCalendar(from, to, viewer);
        responseWriter.writeJsonResponse(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
    }
}
