package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;

import java.util.Collections;
import java.util.List;

/** An intentionally blank Effect has no printed deployment destination or game-text action. */
public abstract class AbstractBlankEffect extends AbstractNormalEffect {
    protected AbstractBlankEffect(Side side, float destiny, String title, Uniqueness uniqueness,
                                  ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, null, title, uniqueness, expansionSet, rarity);
    }

    @Override
    protected List<PlayCardOption> getPlayCardOptions(String playerId, SwccgGame game) {
        return Collections.emptyList();
    }
}
