package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Rebel
 * Title: Jek Porkins
 */
public class Card1210_102 extends AbstractRebel {
    public Card1210_102() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, Title.Jek, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Piloted Red 6 at Battle of Yavin. Specializes in strafing runs. Served in Tierfon Yellow Aces squadron at Tierfon Rebel Outpost. Free trader from Bestine system.");
        setGameText("Adds 2 to power of anything he pilots. While piloting Red 6: it is Heroic, immune to attrition < 4, Jek draws one battle destiny if unable to otherwise, and during your control phase, may use 1 Force to retrieve a Rogue squadron pilot or an X-Wing.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeyword(Keyword.RED_SQUADRON);
        setMatchingStarshipFilter(Filters.Red_6);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingRed6 = new PilotingCondition(self, Filters.Red_6);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.Red_6, Filters.hasPiloting(self)), pilotingRed6, Keyword.HEROIC));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.Red_6, Filters.hasPiloting(self)), pilotingRed6, 4));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingRed6, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__JEK_PORKINS_V__RETRIEVE_CARD;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isPiloting(game, self, Filters.Red_6)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve Rogue squadron pilot or X-Wing");
            action.setActionMsg("Use 1 Force to retrieve a Rogue squadron pilot or an X-Wing into hand");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new RetrieveCardIntoHandEffect(action, playerId, Filters.or(Filters.Rogue_Squadron_pilot, Filters.X_wing)));
            return Collections.singletonList(action);
        }
        return null;
    }
}
