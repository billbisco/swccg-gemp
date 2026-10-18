package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfForcePileAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Scoundrel's Charm
 */
public class Card1281_035 extends AbstractNormalEffect {
    public Card1281_035() {
        super(Side.LIGHT, 0, PlayCardZoneOption.ATTACHED, "Scoundrel's Charm", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLore("Sarcastic, Sardonic, Irreverent. Impertinent. Exasperating. Disrespectful. Outrageous and charming. Han was a scoundrel but Leia began to realize that she loved him.");
        setGameText("Deploy from under Scoundrel's Luck on a scoundrel. Once per turn, may peek at the top 3 cards of your Force pile and take one into hand; reshuffle. Once per turn, may use 1 Force to add or subtract 1 from an opponent's just-drawn destiny.");
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
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && game.getGameState().getForcePileSize(playerId) > 0) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at top 3 cards of Force Pile");
            action.setActionMsg("Peek at the top 3 cards of Force Pile and take one into hand");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new PeekAtTopCardsOfForcePileAndChooseCardsToTakeIntoHandEffect(action, playerId, 3, 1, 1));
            action.appendEffect(new ShufflePileEffect(action, self, playerId, playerId, Zone.FORCE_PILE, true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)) {

            OptionalGameTextTriggerAction add = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            add.setText("Add 1 to opponent's destiny");
            add.appendUsage(new OncePerTurnEffect(add));
            add.appendCost(new UseForceEffect(add, playerId, 1));
            add.appendEffect(new ModifyDestinyEffect(add, 1));
            actions.add(add);

            OptionalGameTextTriggerAction subtract = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            subtract.setText("Subtract 1 from opponent's destiny");
            subtract.appendUsage(new OncePerTurnEffect(subtract));
            subtract.appendCost(new UseForceEffect(subtract, playerId, 1));
            subtract.appendEffect(new ModifyDestinyEffect(subtract, -1));
            actions.add(subtract);
        }
        return actions;
    }
}
