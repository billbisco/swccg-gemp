package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.BattleDestinyMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionLimitedToModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Character
 * Subtype: Droid
 * Title: Leebo
 */
public class Card1291_045 extends AbstractDroid {
    public Card1291_045() {
        super(Side.LIGHT, 3, 2, 2, 4, "Leebo", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setArmor(4);
        setLore("Smuggler. Heavily modified Cybot Galactica LE-series repair droid. Information broker. Provides information gained through underworld channels to the Alliance.");
        setGameText("Adds 2 to the power of anything he pilots. While piloting, draws one battle destiny if unable to otherwise, and your battle destiny draws (and total battle destiny) here may not be reduced. During battle, if aboard Outrider, may use 1 Force: all immunity to attrition here is limited to < 5.");
        addIcons(Icon.PILOT, Icon.PRESENCE);
        addModelType(ModelType.MAINTENANCE);
        addKeywords(Keyword.SMUGGLER, Keyword.INFORMATION_BROKER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        PilotingCondition piloting = new PilotingCondition(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, piloting, 1));
        modifiers.add(new BattleDestinyMayNotBeReducedModifier(self, Filters.sameLocation(self), piloting, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LEEBO__LIMIT_IMMUNITY_TO_ATTRITION;

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.sameLocation(self))
                && new AboardCondition(self, Filters.Outrider).isFulfilled(game.getGameState(), game.getModifiersQuerying())
                && GameConditions.canUseForce(game, playerId, 1)) {
            final PhysicalCard battleLocation = game.getGameState().getBattleLocation();

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Limit immunity to attrition here to < 5");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new ImmunityToAttritionLimitedToModifier(self, Filters.at(battleLocation), 5),
                            "Limits all immunity to attrition here to < 5"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
