package yh.fabulousstars.hangman.server;

import com.google.appengine.api.datastore.*;
import yh.fabulousstars.hangman.game.GameEvent;
import yh.fabulousstars.hangman.game.GameEventType;
import yh.fabulousstars.hangman.game.GameState;
import yh.fabulousstars.hangman.server.utils.EntityUtils;
import yh.fabulousstars.hangman.utils.ObjectHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class BaseServlet extends HttpServlet {
    public static final String PLAYER_TYPE = "Player";
    public static final String GAME_TYPE = "Game";
    protected static final String GAME_STATE_TYPE = "GameState";
    protected static final String EVENT_TYPE = "Event";
    private static final long ENTITY_EXPIRY_MS = 600 * 1000; // 10
    private final DatastoreService datastore; // google datastore service api

    private final String[] cleanupList = new String[] { PLAYER_TYPE, GAME_TYPE, GAME_STATE_TYPE, EVENT_TYPE };

    protected BaseServlet() {
        super();
        this.datastore = DatastoreServiceFactory.getDatastoreService(
                DatastoreServiceConfig.Builder.withDefaults()
        );
    }

    /**
     * Handle GET
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        var session = req.getSession();
        var path = req.getPathInfo();
        if (path != null) {
            var endpoint = req.getPathInfo().substring(1);
            handleRequest(new RequestContext(endpoint, session.getId(), req, resp));
        } else {
            resp.sendRedirect("/");
        }
    }

    /**
     * Get entity from database.
     *
     * @param type
     * @param id
     * @return entity
     */
    protected Entity getEntity(String type, String id) {
        try {
            return datastore.get(
                    KeyFactory.createKey(type, id)
            );
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * Put entity.
     * Add expires property.
     * @param entity
     * @return
     */
    protected Key putEntity(Entity entity) {
        entity.setProperty("expires", new Date(System.currentTimeMillis() + ENTITY_EXPIRY_MS));
        return datastore.put(entity);
    }

    /**
     * Prepare datastore query.
     * @param query
     * @return
     */
    protected PreparedQuery prepare(Query query) {
        return datastore.prepare(query);
    }

    /**
     * Delete entity.
     * @param key
     */
    protected void delete(Key key) {
        datastore.delete(key);
    }

    /**
     * Delete entities.
     * @param keys
     */
    protected void delete(List<Key> keys) {
        datastore.delete(keys);
    }

    /**
     * Gut stati into db.
     *
     * @param gameId
     * @return
     */
    protected GameState getGameState(String gameId) {
        var entity = getEntity(GAME_STATE_TYPE, gameId);
        if (entity != null) {
            return (GameState) EntityUtils.getBlobObject(entity);
        }
        return null;
    }

    /**
     * Gut stati into db.
     *
     * @param gameId
     */
    protected void putGameState(String gameId, GameState gameState) {
        var entity = new Entity(GAME_STATE_TYPE, gameId);
        EntityUtils.putBlobObject(entity, gameState);
        putEntity(entity);
    }

    /**
     * Get all entity ids.
     *
     * @return list of ids
     */
    protected List<String> getAllIds(String type) {
        var ids = new ArrayList<String>();
        var iter = datastore.prepare(new Query(type).setKeysOnly()).asIterator();
        while (iter.hasNext()) {
            var entity = iter.next();
            ids.add(entity.getKey().getName());
        }
        return ids;
    }

    /**
     * Add event to database for polling.
     *
     * @param clientId target client.
     * @param event    Event object
     */
    protected void addEvent(String clientId, GameEvent event) {
        var entity = new Entity(EVENT_TYPE);
        entity.setProperty("target", clientId); // client
        EntityUtils.putBlobObject(entity, event);
        putEntity(entity);
    }

    /**
     * Poll and return oldest event, removing it from database.
     * The event is sent to the client as a json body.
     *
     * @param ctx
     * @throws IOException
     */
    protected void poll(RequestContext ctx) throws IOException {
        // set body type
        ctx.resp().setContentType("application/octet-stream");
        // get reader
        var output = ctx.resp().getOutputStream();
        // query oldest first
        var entityIter = datastore.prepare(
                new Query(EVENT_TYPE)
                        .setFilter(new Query.FilterPredicate(
                                "target", Query.FilterOperator.EQUAL, ctx.session())
                        )
                        .addSort("expires", Query.SortDirection.ASCENDING)
        ).asIterator();
        if (entityIter.hasNext()) { // get one
            var entity = entityIter.next();
            var bytes = EntityUtils.getBlobBytes(entity);
            datastore.delete(entity.getKey());
            output.write(bytes);
        } else { // no events. send idle
            // empty
            var idle = ObjectHelper.toBytes(new GameEvent(GameEventType.Idle));
            output.write(idle);
        }
    }

    /**
     * Cleanup expired entities.
     * @param ctx
     */
    protected void cleanup(RequestContext ctx) {
        if(ctx.req().getHeader("X-Appengine-Cron") != null) { // app engine cron call
            var now = new Date();
            var keys = new ArrayList<Key>();
            for (var type : cleanupList) {
                var iter = prepare(new Query(type)
                        .setFilter(new Query.FilterPredicate("expires", Query.FilterOperator.LESS_THAN_OR_EQUAL, now))
                        .setKeysOnly()
                ).asIterator();
                while (iter.hasNext()) {
                    keys.add(iter.next().getKey());
                }
            }
            if (!keys.isEmpty()) {
                datastore.delete(keys);
            }
        }
    }

    /**
     * Handle requests from get and put in subclass.
     *
     * @param ctx
     * @throws IOException
     */
    protected abstract void handleRequest(RequestContext ctx) throws IOException;
}
