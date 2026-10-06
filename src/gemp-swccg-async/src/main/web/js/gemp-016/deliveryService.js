var deliveryDialogs = {};
var deliveryState = {};

var DELIVERY_TILES_PER_PAGE = 120;
var DELIVERY_MIN_CARD_WIDTH = 70;
var DELIVERY_MIN_CARD_HEIGHT = 98;

function deliveryService(xml) {
    log("Delivered a package:");
    log(xml);

    var root = xml.documentElement;
    if (root.tagName != "delivery")
        return;

    var collections = root.getElementsByTagName("collectionType");
    for (var i = 0; i < collections.length; i++) {
        var collection = collections[i];
        var collectionName = collection.getAttribute("name");
        ensureDeliveryDialog(collectionName);

        var incoming = readDeliveryItems(collection);
        var state = deliveryState[collectionName];
        state.items = mergeDeliveryItems(state.items, incoming);
        sortDeliveryItems(state.items);
        state.pages = paginateDeliveryTiles(state.items, DELIVERY_TILES_PER_PAGE);
        if (state.pageIndex >= state.pages.length)
            state.pageIndex = Math.max(0, state.pages.length - 1);

        openSizeDialog(deliveryDialogs[collectionName]);
        renderDeliveryDialog(collectionName);
    }
}

function ensureDeliveryDialog(collectionName) {
    if (deliveryDialogs[collectionName] != null)
        return;

    var dialog = $("<div class='delivery-dialog'></div>").dialog({
        title: "New items - " + collectionName,
        autoOpen: false,
        closeOnEscape: false,
        resizable: true,
        width: 250,
        height: 150
    });
    deliveryDialogs[collectionName] = dialog;
    deliveryState[collectionName] = {items: [], pages: [], pageIndex: 0};

    dialog.bind("dialogresize", function () {
        renderDeliveryDialog(collectionName);
    });
    dialog.bind("dialogclose", function () {
        deliveryState[collectionName] = {items: [], pages: [], pageIndex: 0};
        dialog.html("");
    });
}

function readDeliveryItems(collection) {
    var items = [];
    var packs = collection.getElementsByTagName("pack");
    for (var j = 0; j < packs.length; j++) {
        var packElem = packs[j];
        items.push({
            kind: "pack",
            blueprintId: packElem.getAttribute("blueprintId"),
            count: packElem.getAttribute("count") || "1",
            title: packElem.getAttribute("title") || packElem.getAttribute("blueprintId"),
            horizontal: false
        });
    }
    var cards = collection.getElementsByTagName("card");
    for (var k = 0; k < cards.length; k++) {
        var cardElem = cards[k];
        items.push({
            kind: "card",
            blueprintId: cardElem.getAttribute("blueprintId"),
            count: cardElem.getAttribute("count") || "1",
            title: cardElem.getAttribute("title") || "",
            horizontal: cardElem.getAttribute("horizontal")
        });
    }
    return items;
}

function mergeDeliveryItems(existing, incoming) {
    var map = {};
    var order = [];
    function add(item) {
        var key = item.kind + ":" + item.blueprintId;
        if (map[key] == null) {
            map[key] = {
                kind: item.kind,
                blueprintId: item.blueprintId,
                count: String(parseInt(item.count, 10) || 1),
                title: item.title || "",
                horizontal: item.horizontal
            };
            order.push(key);
        } else {
            var next = (parseInt(map[key].count, 10) || 0) + (parseInt(item.count, 10) || 0);
            map[key].count = String(next);
            if (!map[key].title && item.title)
                map[key].title = item.title;
        }
    }
    for (var i = 0; i < existing.length; i++)
        add(existing[i]);
    for (var j = 0; j < incoming.length; j++)
        add(incoming[j]);
    var merged = [];
    for (var n = 0; n < order.length; n++)
        merged.push(map[order[n]]);
    return merged;
}

function deliverySetKey(blueprintId) {
    var match = String(blueprintId || "").match(/^(\d+)/);
    return match ? parseInt(match[1], 10) : 10000;
}

function sortDeliveryItems(items) {
    items.sort(function (a, b) {
        var setA = deliverySetKey(a.blueprintId);
        var setB = deliverySetKey(b.blueprintId);
        if (setA !== setB)
            return setA - setB;
        var nameA = (a.title || a.blueprintId || "").toLowerCase();
        var nameB = (b.title || b.blueprintId || "").toLowerCase();
        if (nameA < nameB)
            return -1;
        if (nameA > nameB)
            return 1;
        return String(a.blueprintId).localeCompare(String(b.blueprintId));
    });
}

function paginateDeliveryTiles(items, pageSize) {
    var pages = [];
    var page = [];
    for (var i = 0; i < items.length; i++) {
        if (page.length >= pageSize) {
            pages.push(page);
            page = [];
        }
        page.push(items[i]);
    }
    if (page.length > 0)
        pages.push(page);
    return pages;
}

function renderDeliveryDialog(collectionName) {
    var dialog = deliveryDialogs[collectionName];
    var state = deliveryState[collectionName];
    if (dialog == null || state == null)
        return;

    dialog.html("");
    var pager = $("<div class='delivery-pager'></div>");
    var viewport = $("<div class='delivery-page'></div>");
    var inner = $("<div class='delivery-page-inner'></div>");
    viewport.append(inner);
    dialog.append(pager);
    dialog.append(viewport);

    var pageCount = state.pages.length;
    if (pageCount < 1)
        pageCount = 1;
    if (state.pageIndex < 0)
        state.pageIndex = 0;
    if (state.pageIndex > pageCount - 1)
        state.pageIndex = pageCount - 1;

    var prev = $("<button type='button' class='delivery-page-arrow' title='Previous page'>&lt;</button>");
    var next = $("<button type='button' class='delivery-page-arrow' title='Next page'>&gt;</button>");
    var label = $("<span class='delivery-page-label'></span>");
    label.text("Page " + (state.pageIndex + 1) + " of " + pageCount);
    prev.prop("disabled", state.pageIndex <= 0);
    next.prop("disabled", state.pageIndex >= pageCount - 1);
    prev.click(function () {
        if (state.pageIndex > 0) {
            state.pageIndex--;
            renderDeliveryDialog(collectionName);
        }
    });
    next.click(function () {
        if (state.pageIndex < pageCount - 1) {
            state.pageIndex++;
            renderDeliveryDialog(collectionName);
        }
    });
    pager.append(prev).append(label).append(next);

    var pageItems = state.pages[state.pageIndex] || [];
    var cardDivs = [];
    for (var i = 0; i < pageItems.length; i++) {
        var item = pageItems[i];
        var card = new Card(item.blueprintId, null, null, item.horizontal, "delivery", "delivery-" + collectionName + "-" + i, "player");
        var count = parseInt(item.count, 10) || 1;
        card.tokens = {"count": count};
        card.deliveryCountBadge = true;
        var cardDiv = Card.CreateCardDiv(card.imageUrl, card.testingText, null, card.isFoil(), true, false, card.incomplete);
        cardDiv.data("card", card);
        inner.append(cardDiv);
        cardDivs.push(cardDiv);
    }
    layoutDeliveryTiles(inner, cardDivs);
}

function layoutDeliveryTiles(inner, cardDivs) {
    var minW = DELIVERY_MIN_CARD_WIDTH;
    var minH = DELIVERY_MIN_CARD_HEIGHT;
    var pad = 4;
    var width = inner.parent().width();
    if (!width || width < minW + pad * 2)
        width = $(window).width() * 0.8 - 24;
    var x = pad;
    var y = pad;
    for (var i = 0; i < cardDivs.length; i++) {
        if (x + minW + pad > width && x > pad) {
            x = pad;
            y += minH + pad;
        }
        layoutCardElem(cardDivs[i], x, y, minW, minH, 10);
        layoutTokens(cardDivs[i]);
        x += minW + pad;
    }
    inner.css({position: "relative", height: (y + minH + pad) + "px"});
}
