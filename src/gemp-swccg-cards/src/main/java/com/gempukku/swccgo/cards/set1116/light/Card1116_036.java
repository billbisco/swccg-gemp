package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Alien
 * Title: Sergeant Doallyn
 */
public class Card1116_036 extends AbstractAlien {
    public Card1116_036() {
        super(Side.LIGHT, 4, 3, 3, 3, 4, Title.Doallyn, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setLore("Humanoid mercenary. Gambler. Blackmailed into working for Jabba. Friend of Yarna d'al' Gargan. Expert in breathing apparatus. Needs Hydron 3 cartridges to breathe.");
        setGameText("Heroic. Smuggler. When deployed, may play a Defensive Shield from under your Starting Effect (as if from hand). Vibro-Ax is a matching weapon for (and deploys for free on) Doallyn, and when using it, it fires for free. Immune to attrition < 4.");
        addIcons(Icon.WARRIOR, Icon.JABBAS_PALACE);
        addKeywords(Keyword.GAMBLER, Keyword.MERCENARY, Keyword.SMUGGLER, Keyword.HEROIC);
        addPersona(Persona.DOALLYN);
        setMatchingWeaponFilter(Filters.Vibro_Ax);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter vibroAx = Filters.and(Filters.Vibro_Ax, Filters.attachedTo(self));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.Vibro_Ax, self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        modifiers.add(new FiresForFreeModifier(self, vibroAx));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
            if (startingEffect != null) {
                Filter filter = Filters.Defensive_Shield;
                if (GameConditions.hasStackedCards(game, startingEffect, filter)) {
                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Play a Defensive Shield");
                    action.appendTargeting(
                            new ChooseStackedCardEffect(action, playerId, startingEffect, filter) {
                                @Override
                                protected void cardSelected(PhysicalCard selectedCard) {
                                    action.appendEffect(new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
