package com.gempukku.swccgo.cards.set1103.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Character
 * Subtype: Alien
 * Title: Dannik Jerriko
 */
public class Card1103_030 extends AbstractAlien {
    public Card1103_030() {
        super(Side.DARK, 2, 4, 1, 3, 2, "Dannik Jerriko", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Anzati assassin. Cheek-folds hide proboscises which allow him to 'eat the soup' (consume the life Force) of his victims. Smokes t'bac. Currently working for Jabba.");
        setGameText("If present where you just won a battle, may use 1 Force to place a card from opponent's Lost Pile out of play.");
        addPersona(Persona.DANNIK);
        addIcons(Icon.A_NEW_HOPE);
        setSpecies(Species.ANZATI);
        addKeyword(Keyword.ASSASSIN);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        if (TriggerConditions.wonBattleAt(game, effectResult, playerId, Filters.wherePresent(self))
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.hasLostPile(game, opponent)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place card from Lost Pile out of play");
            action.setActionMsg("Place a card from opponent's Lost Pile out of play");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, opponent, Filters.any, false));
            return Collections.singletonList(action);
        }
        return null;
    }
}
