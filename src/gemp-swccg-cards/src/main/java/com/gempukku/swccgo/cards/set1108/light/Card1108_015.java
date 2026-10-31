package com.gempukku.swccgo.cards.set1108.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardsEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Character
 * Subtype: Rebel
 * Title: Princess Leia Organa
 */
public class Card1108_015 extends AbstractRebel {
    public Card1108_015() {
        super(Side.LIGHT, 1, 0, 3, 4, 7, Title.Princess_Leia_Organa, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Captured by Jabba. Princess Leia provided a distraction for his henchbeings. Waiting for the first chance to escape. Really made Jabba's tail wiggle.");
        setGameText("* Deploys free only as an escorted captive of Jabba or a bounty hunter. Your Strangle destiny draws are +1. 'Escort alone' on Strangle is treated as 'escort'. When released, escort is lost and you may retrieve any three cards (except Strangle).");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.JABBAS_PALACE);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
        addPersona(Persona.LEIA);
        setDeploysAsCapturedPrisoner(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeModifier(self));
        return modifiers;
    }

    @Override
    protected Filter getValidDeployTargetFilterForCardType(String playerId, final SwccgGame game, final PhysicalCard self, boolean isSimDeployAttached, boolean ignorePresenceOrForceIcons, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption) {
        return Filters.or(Filters.bounty_hunter, Filters.Jabba);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpotFromAllOnTable(game, Filters.or(Filters.bounty_hunter, Filters.Jabba));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        return getStrangleModifiers(self);
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return getStrangleModifiers(self);
    }

    private List<Modifier> getStrangleModifiers(PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Strangle, ModifyGameTextType.LEGACY__LEIA_ORGANA_V__STRANGLE_ESCORT_ALONE_IS_ESCORT));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId retrieveId = GameTextActionId.LEGACY__LEIA_ORGANA_V__RETRIEVE_CARDS;
        if (TriggerConditions.released(game, effectResult, self)
                && GameConditions.canSearchLostPile(game, playerId, self, retrieveId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, retrieveId);
            action.setText("Retrieve three cards");
            action.appendEffect(
                    new RetrieveCardsEffect(action, playerId, 3, Filters.not(Filters.Strangle)));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersWhenInactiveInPlay(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return getGameTextOptionalAfterTriggers(playerId, game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        if (TriggerConditions.isDestinyJustDrawnFor(game, effectResult, Filters.and(Filters.your(self), Filters.Strangle))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 1 to Strangle destiny");
            action.appendEffect(
                    new ModifyDestinyEffect(action, 1));
            actions.add(action);
        }
        if (TriggerConditions.released(game, effectResult, self)) {
            PhysicalCard escort = self.getAttachedTo();
            if (escort != null && Filters.character.accepts(game, escort)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make escort lost");
                action.appendEffect(
                        new LoseCardFromTableEffect(action, escort));
                actions.add(action);
            }
        }
        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhenInactiveInPlay(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return getGameTextRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
    }
}
