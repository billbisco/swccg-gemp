package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtScompLink;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.1
 * Type: Character
 * Subtype: Droid
 * Title: R2-D2, Subversive Droid
 */
public class Card1271_035 extends AbstractDroid {
    public Card1271_035() {
        super(Side.LIGHT, 2, 2, 1, 3, "R2-D2, Subversive Droid", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setAlternateDestiny(5);
        setLore("Spy.");
        setGameText("Captured if an Imperial controls this site. While present with a Scomp link at a battleground (or mobile) site, your total power at adjacent sites is +2 and, once per turn, may use 1 Force to add or subtract 1 from opponent's just drawn destiny.");
        addPersona(Persona.R2D2);
        addModelType(ModelType.ASTROMECH);
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.SPY);
    }

    private Condition connectedAtQualifyingSite(PhysicalCard self) {
        return new AndCondition(new PresentAtCondition(self, Filters.and(Filters.site,
                Filters.or(Filters.battleground, Filters.mobile_site))), new PresentAtScompLink(self));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new TotalPowerModifier(self, Filters.adjacentSite(self),
                connectedAtQualifyingSite(self), 2, self.getOwner()));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isAtLocation(game, self, Filters.site)
                && GameConditions.controlsWith(game, self, opponent, Filters.here(self), Filters.Imperial)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setPerformingPlayer(opponent);
            action.setText("Capture R2-D2");
            action.appendEffect(new CaptureCharacterOnTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId id = GameTextActionId.LEGACY__R2D2_SUBVERSIVE_DROID__MODIFY_DESTINY;
        if (!TriggerConditions.isDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId))
                || !connectedAtQualifyingSite(self).isFulfilled(game.getGameState(), game.getModifiersQuerying())
                || !GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, id)
                || !GameConditions.canUseForce(game, playerId, 1)
                || game.getModifiersQuerying().mayNotModifyDestinyDraw(game.getGameState(), playerId)
                || game.getGameState().getTopDrawDestinyState() == null
                || game.getGameState().getTopDrawDestinyState().getDrawDestinyEffect().isDestinyReset())
            return null;
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();
        for (int amount : new int[]{1, -1}) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, id);
            action.setText(amount > 0 ? "Add 1 to opponent's destiny" : "Subtract 1 from opponent's destiny");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendEffect(new ModifyDestinyEffect(action, amount));
            actions.add(action);
        }
        return actions;
    }
}
