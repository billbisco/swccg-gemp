package com.gempukku.swccgo.cards.set1108.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Starship
 * Subtype: Capital
 * Title: Jabba's Space Cruiser (V)
 */
public class Card1108_039 extends AbstractCapitalStarship {
    public Card1108_039() {
        super(Side.DARK, 2, 4, 5, 5, null, 4, 6, Title.Jabbas_Space_Cruiser, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Flying fortress of Jabba Desilijic Tiure. Reaches speeds of 800 kph in atmosphere. The crime lord installed hidden gunports as an unpleasant surprise for would-be pirates.");
        setGameText("May add 2 alien pilots and 6 passengers. May deploy an [Independent] starship (with a pilot aboard, if unpiloted) here from hand and/or Reserve Deck (for free) as a 'react'; reshuffle. If Jabba here, immune to attrition.");
        addIcons(Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.SPECIAL_EDITION);
        addModelType(ModelType.UBRIKKIAN_LUXURY_SPACE_YACHT);
        addKeywords(Keyword.CRUISER);
        setPilotCapacity(2);
        setPassengerCapacity(6);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.alien;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self, new HereCondition(self, Filters.Jabba)));
        modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy Independent starship as a react", self.getOwner(),
                Filters.and(Filters.your(self), Icon.INDEPENDENT, Filters.starship), Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersEvenIfUnpiloted(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__JABBAS_SPACE_CRUISER__DEPLOY_INDEPENDENT;
        Filter here = Filters.here(self);
        if ((TriggerConditions.battleInitiatedAt(game, effectResult, here)
                || TriggerConditions.forceDrainInitiatedAt(game, effectResult, here))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Independent starship as a 'react'");
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.your(self), Icon.INDEPENDENT, Filters.starship), here, true, true, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
