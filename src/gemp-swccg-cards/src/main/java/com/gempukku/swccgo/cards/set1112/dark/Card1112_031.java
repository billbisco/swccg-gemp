package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHavePowerReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Character
 * Subtype: Republic
 * Title: Daultay Dofine
 */
public class Card1112_031 extends AbstractRepublic {
    public Card1112_031() {
        super(Side.DARK, 2, 3, 2, 3, 4, "Daultay Dofine", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("Neimoidian Trade Federation captain who gained his current position through political backstabbing and family connections. Not favored by Darth Sidious.");
        setGameText("Adds 3 to power of anything he pilots. Blockade Flagship is immune to attrition < 9. While piloting Blockade Flagship and your [Theed Palace] Admiral's Order on table, Blockade Flagship may not be targeted by weapons or have its power reduced. Once per game, may take Gunray or Haako into hand from Reserve Deck; reshuffle.");
        addPersona(Persona.DOFINE);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingWithOrder = new AndCondition(
                new PilotingCondition(self, Filters.Blockade_Flagship),
                new OnTableCondition(self, Filters.and(Filters.your(self), Filters.Admirals_Order, Icon.THEED_PALACE)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.Blockade_Flagship, 9));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.Blockade_Flagship, pilotingWithOrder));
        modifiers.add(new MayNotHavePowerReducedModifier(self, Filters.Blockade_Flagship, pilotingWithOrder, self.getOwner()));
        modifiers.add(new MayNotHavePowerReducedModifier(self, Filters.Blockade_Flagship, pilotingWithOrder, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CAPTAIN_DAULTAY_DOFINE__UPLOAD_ACTIVATE_THE_DROIDS;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Gunray or Haako into hand");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Gunray, Filters.Haako), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
