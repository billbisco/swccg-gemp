package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleActionProxyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.3
 * Type: Character
 * Subtype: Alien
 * Title: Cad Bane
 */
public class Card1263_001 extends AbstractAlien {
    public static final String PONDA_BABAS_HOLD_OUT_BLASTER = "Ponda Baba's Hold-out Blaster";

    public Card1263_001() {
        super(Side.DARK, 1, 4, 4, 2, 4, "Cad Bane", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setLore("Bounty hunter.");
        setGameText("Adds 2 to power of anything he pilots. Ponda Baba's Hold-out Blaster deploys and fires for free on Bane. Once per turn, if Bane just used a weapon to hit a character, may 'annihilate' target (target is placed out of play when forfeited this battle).");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.EPISODE_I);
        addKeywords(Keyword.BOUNTY_HUNTER);
        addPersona(Persona.CAD);
        setSpecies(Species.DUROS);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter holdOutBlaster = Filters.title(PONDA_BABAS_HOLD_OUT_BLASTER);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DeploysFreeToTargetModifier(self, holdOutBlaster, self));
        modifiers.add(new FiresForFreeModifier(self, Filters.and(holdOutBlaster, Filters.attachedTo(self))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CAD_BANE__ANNIHILATE;
        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, Filters.weapon, Filters.sameCardId(self))
                && GameConditions.isDuringBattle(game)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            final PhysicalCard target = ((HitResult) effectResult).getCardHit();
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("'Annihilate' " + GameUtils.getFullName(target));
            action.setActionMsg("'Annihilate' " + GameUtils.getCardLink(target) + " (placed out of play when forfeited this battle)");
            action.addAnimationGroup(target);
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new AddUntilEndOfBattleActionProxyEffect(action, new AbstractActionProxy() {
                @Override
                public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                    Filter targetFilter = Filters.sameCardId(target);
                    if (TriggerConditions.justForfeited(game, effectResult, playerId, targetFilter)
                            || TriggerConditions.justForfeited(game, effectResult, game.getOpponent(playerId), targetFilter)) {
                        RequiredGameTextTriggerAction annihilate = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                        annihilate.setText("Place " + GameUtils.getFullName(target) + " out of play");
                        annihilate.setActionMsg("Place 'annihilated' " + GameUtils.getCardLink(target) + " out of play");
                        annihilate.appendEffect(new PlaceCardOutOfPlayFromOffTableEffect(annihilate, target));
                        return Collections.<TriggerAction>singletonList(annihilate);
                    }
                    return null;
                }
            }));
            return Collections.singletonList(action);
        }
        return null;
    }
}
