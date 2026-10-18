package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Starship
 * Subtype: Starfighter
 * Title: Obsidian 10
 */
public class Card1220_075 extends AbstractStarfighter {
    public Card1220_075() {
        super(Side.DARK, 3, 1, 1, null, 3, null, 3, Title.Obsidian_10, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Recalled to defend second Death Star during construction. Stationed aboard Thunderflare.");
        setGameText("Heroic. May add 1 pilot. OS-72-10 deploys -2 aboard. When lost from Endor, a mobile system, or a sector, may place this starship (and cards aboard it) in owner's Used Pile.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.OBSIDIAN_SQUADRON, Keyword.NO_HYPERDRIVE, Keyword.HEROIC);
        addModelType(ModelType.TIE_LN);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.OS_72_10);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.OS_72_10, -2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.OS_72_10, -2, self));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersWhenInactiveInPlay(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLostFromLocation(game, effectResult, self, Filters.or(Filters.Endor_location, Filters.mobile_system, Filters.sector))) {
            PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(justLostCard) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(justLostCard) + " (and cards aboard it) in Used Pile");
            action.appendEffect(new PutCardFromLostPileInUsedPileEffect(action, playerId, justLostCard, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
