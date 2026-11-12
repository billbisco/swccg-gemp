package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Tusken Raider
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (December 1995) text, from wiki.swccg.com [[Tusken Raider]] (Decipher archive scan; power box "*").
 * The total power of a group present together is 1, 3, 6 or 10 for one to four Tusken Raiders: played as Decipher's
 * own reading (Card1_196d) of +1/2 power for each other non-unique Tusken Raider present, at most three counted. As
 * printed there is also a limit of four at one site: this card may not deploy or move to a site where four Tusken
 * Raiders are already present.
 */
public class Card1_196o extends Card1_196 {
    private static final int LIMIT_AT_ONE_SITE = 4;

    public Card1_196o() {
        super();
        setGameText("*Total power of a group of Tusken Raiders depends on how many are present. One has a total power of 1, two = 3, three = 6, four = 10. Limit of four at one site. Deploy only to Tatooine, but may move elsewhere.");
        setTextState(new TextState("1_196", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(super.getGameTextValidDeployTargetFilter(game, self, playCardOptionId, asReact), Filters.not(fullSite(self)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(Card1_196d.groupPowerModifier(self));
        modifiers.add(new MayNotMoveToLocationModifier(self, Filters.and(Filters.site, fullSite(self))));
        return modifiers;
    }

    /** Accepts a card at (or a location that is) a site where four other Tusken Raiders are present. */
    private static Filter fullSite(final PhysicalCard self) {
        final int permSourceCardId = self.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard location = modifiersQuerying.getLocationHere(gameState, physicalCard);
                if (location == null)
                    return false;
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                return Filters.countActive(gameState.getGame(), source, Filters.and(Filters.not(Filters.sameCardId(source)),
                        Filters.Tusken_Raider, Filters.presentAt(Filters.sameCardId(location)))) >= LIMIT_AT_ONE_SITE;
            }
        };
    }
}
