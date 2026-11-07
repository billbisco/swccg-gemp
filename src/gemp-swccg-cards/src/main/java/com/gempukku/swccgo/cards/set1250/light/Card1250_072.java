package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Legacy Block 5 Coruscant: Jedi Council Chamber (V), printing 72. */
public class Card1250_072 extends AbstractSite {
    public Card1250_072() {
        super(Side.LIGHT, Title.Jedi_Council_Chamber, Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("May not be deployed if your Senate on table. If you just deployed a Jedi Council Member here, may take any one card into hand from Used Pile; reshuffle.");
        setLocationDarkSideGameText("If your non-battleground site on table with no related locations (or if you do not control a battleground site), [Virtual Block 5] Wipe Them Out, All Of Them is suspended.");
        addIcon(Icon.LIGHT_FORCE, 3);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        return !GameConditions.canSpot(game, self, Filters.and(Filters.your(playerId), Filters.Galactic_Senate));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId id = GameTextActionId.LEGACY__JEDI_COUNCIL_CHAMBER__TAKE_FROM_USED_PILE;
        if (!TriggerConditions.justDeployedToLocation(game, result, playerId, Filters.Jedi_Council_member, self)
                || !GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, id)) return null;

        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, id);
        action.setText("Take a card into hand from Used Pile");
        action.appendEffect(new TakeCardIntoHandFromUsedPileEffect(action, playerId, true));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerId, SwccgGame game, PhysicalCard self) {
        Filter noRelatedLocations = (state, modifiers, card) ->
                !Filters.canSpot(state.getGame(), self, Filters.relatedLocation(card));
        Filter isolated = Filters.and(Filters.your(playerId), Filters.site, Filters.not(Filters.battleground), noRelatedLocations);
        if (!GameConditions.controls(game, playerId, Filters.battleground_site)
                || GameConditions.canSpot(game, self, isolated)) {
            return List.of(new SuspendsCardModifier(self,
                    Filters.and(Icon.LEGACY_BLOCK_5, Filters.title("Wipe Them Out, All Of Them"))));
        }
        return null;
    }
}
