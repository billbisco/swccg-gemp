package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.evaluators.MaxEvaluator;
import com.gempukku.swccgo.cards.evaluators.MinEvaluator;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.4
 * Type: Character
 * Subtype: Jedi Master
 * Title: Mace Windu, Master Of The Order
 */
public class Card1274_015 extends AbstractJediMaster {
    public Card1274_015() {
        super(Side.LIGHT, 0, 7, 6, 7, 8, "Mace Windu, Master Of The Order", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setLore("Senior Jedi Council member who maintains rigorous adherence to the Code. Sent Qui-Gon to Naboo to accompany the Queen and learn more about the mysterious 'dark warrior'.");
        setGameText("* = 1 or number of Jedi on table (to a maximum of 3), whichever is higher. Mace's forfeit may not be reduced and his total weapon destiny is +1. If a Dark Jedi was just lost from table, may retrieve 1 Force. Immune to attrition.");
        addPersona(Persona.MACE);
        addIcons(Icon.WARRIOR, Icon.EPISODE_I, Icon.CORUSCANT);
        addKeywords(Keyword.JEDI_COUNCIL_MEMBER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDestinyModifier(self,
                new MaxEvaluator(new ConstantEvaluator(1),
                        new MinEvaluator(new ConstantEvaluator(3), new OnTableEvaluator(self, Filters.Jedi)))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotHaveForfeitValueReducedModifier(self, self));
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.any, Filters.sameCardId(self), 1));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__MACE_WINDU_MASTER_OF_THE_ORDER__RETRIEVE_FORCE;
        if (TriggerConditions.justLost(game, effectResult, Filters.Dark_Jedi)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 1 Force");
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
