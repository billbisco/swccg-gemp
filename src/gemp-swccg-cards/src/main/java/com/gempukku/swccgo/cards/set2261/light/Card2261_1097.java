package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.cards.set601.light.Card601_051;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TakeCardFromVoidIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeOneCardIntoHandFromOffTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Let The Wookiee Win (V)
 * PC-E1 version of 1261_97: the PC website card list text (by 2012-07-21): STARTING deploys an always [Immune to Alter] Effect and two <> sites. The Virtual Cards Master List
 * (2013-04-27, PC-E3) repeats the release-sheet words and is the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1097 extends Card601_051 {
    public Card2261_1097() {
        super();
        setGameText("USED: Reveal the top 3 cards of Reserve Deck; place one in hand, and the others on bottom of Used Pile (in any order). STARTING: If your starting location was a non-[Special Edition] location, deploy from Reserve Deck an always [Immune to Alter] Effect and two <> sites with < 3 total [Light Side Force]. Place this Interrupt in hand.");
        setLegacyVersion(new LegacyVersion("1261_97", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }

    @Override
    protected Filter startingEffectFilter() {
        return Filters.and(Filters.Effect, Filters.always_immune_to_Alter);
    }

    @Override
    protected int maxStartingSites() {
        return 2;
    }
}
