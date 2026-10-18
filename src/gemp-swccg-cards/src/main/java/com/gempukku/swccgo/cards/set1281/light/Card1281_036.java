package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Scoundrel's Ingenuity
 */
public class Card1281_036 extends AbstractNormalEffect {
    public Card1281_036() {
        super(Side.LIGHT, 0, PlayCardZoneOption.ATTACHED, "Scoundrel's Ingenuity", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLore("");
        setGameText("Deploy from under Scoundrel's Luck on a scoundrel. Scoundrel is power +2 and adds 1 to his total weapon destiny. During your control phase, may draw destiny. If destiny > 3, your Force drains at same and related locations are +1 for remainder of turn.");
        addIcons(Icon.SCOUNDREL);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        PhysicalCard stackedOn = self.getStackedOn();
        return stackedOn != null && Filters.title("Scoundrel's Luck").accepts(game, stackedOn);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.scoundrel;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.scoundrel;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter scoundrel = Filters.hasAttached(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, scoundrel, 2));
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.any, scoundrel, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw destiny");
            action.setActionMsg("Draw destiny. If destiny > 3, Force drains at same and related locations are +1 for remainder of turn");
            action.appendUsage(new OncePerPhaseEffect(action));
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            GameState gameState = game.getGameState();
                            if (totalDestiny == null) {
                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                return;
                            }
                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                            if (totalDestiny > 3) {
                                gameState.sendMessage("Result: Succeeded");
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new ForceDrainModifier(self, Filters.sameOrRelatedLocation(self), 1, playerId),
                                                "Force drains at same and related locations are +1"));
                            }
                            else {
                                gameState.sendMessage("Result: Failed");
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
