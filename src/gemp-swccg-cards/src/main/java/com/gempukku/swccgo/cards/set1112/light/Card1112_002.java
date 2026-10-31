package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.cards.effects.CancelAttackEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnBottomOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardsEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Effect
 * Title: At Peace
 */
public class Card1112_002 extends AbstractNormalEffect {
    public Card1112_002() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.At_Peace, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("To recover from the strenuous Jedi training routine and revitalize the mind and body, an apprentice must rest to be calm and at peace.");
        setGameText("Deploy on Yoda's Hut. You may not deploy [Permanent Weapon] or [Maintenance] cards. May lose 2 Force to cancel an attack. May place [Endor] Leia from hand on bottom of Reserve Deck. May use 1 Force to deploy a Jedi Test from under your Starting Effect. (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Yodas_Hut;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployModifier(self, Filters.or(Icon.PERMANENT_WEAPON, Icon.MAINTENANCE), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        if (TriggerConditions.attackInitiatedAt(game, effectResult, Filters.any)
                && GameConditions.numCardsInForcePile(game, playerId) >= 2) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel an attack");
            action.appendCost(new LoseForceEffect(action, playerId, 2));
            action.appendEffect(new CancelAttackEffect(action));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        if (GameConditions.hasHand(game, playerId)
                && GameConditions.hasInHand(game, playerId, Filters.and(Filters.Leia, Icon.ENDOR))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Place [Endor] Leia on bottom of Reserve Deck");
            action.appendEffect(new PutCardFromHandOnBottomOfReserveDeckEffect(action, playerId, Filters.and(Filters.Leia, Icon.ENDOR), false));
            actions.add(action);
        }

        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        if (startingEffect != null
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.hasStackedCards(game, startingEffect, Filters.Jedi_Test)) {
            final PhysicalCard stackedOn = startingEffect;
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Deploy a Jedi Test");
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendEffect(new DeployStackedCardsEffect(action, stackedOn, Filters.Jedi_Test, 1, 1, true));
            actions.add(action);
        }
        return actions;
    }
}
