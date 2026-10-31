package com.gempukku.swccgo.cards.set1108.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Character
 * Subtype: Alien
 * Title: Attark
 */
public class Card1108_001 extends AbstractAlien {
    public Card1108_001() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, Title.Attark, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("From a mysterious species known as Hoovers. Possesses tremendous technical abilities. Small size allows him to easy manipulate detailed electronic equipment.");
        setGameText("During your control phase, if No Love For The Empire on table, may retrieve a non-unique alien of a different species than your Rep.");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.HOOVER);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ATTARK_V__RETRIEVE_ALIEN;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canSpot(game, self, Filters.No_Love_For_The_Empire)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            Filter retrieveFilter = Filters.and(Filters.non_unique, Filters.alien);
            PhysicalCard rep = game.getGameState().getRep(playerId);
            if (rep != null && rep.getBlueprint().getSpecies() != null) {
                final Species repSpecies = rep.getBlueprint().getSpecies();
                retrieveFilter = Filters.and(retrieveFilter, Filters.not(Filters.species(repSpecies)));
            }

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve a non-unique alien");
            action.setActionMsg("Retrieve a non-unique alien of a different species than your Rep");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, retrieveFilter));
            return Collections.singletonList(action);
        }
        return null;
    }
}
