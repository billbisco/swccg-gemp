package com.gempukku.swccgo.cards.set1101.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ChooseEffectEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromBottomOfForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromTopOfForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 1: Legacy
 * Type: Starship
 * Subtype: Starfighter
 * Title: Gold 1
 */
public class Card1101_003 extends AbstractStarfighter {
    public Card1101_003() {
        super(Side.LIGHT, 3, 1, 2, null, 3, 4, 3, "Gold 1", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Lead fighter of Gold Squadron at Battle of Yavin. Flown by Jon 'Dutch' Vander. Designated Specter 1 at Renforra Base.");
        setGameText("May add 2 pilots or passengers. If Dutch piloting, power +2, immune to attrition < 4, and once during any control phase, may take the top or bottom card of your Force Pile into hand.");
        addPersona(Persona.GOLD_1);
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.GOLD_SQUADRON);
        addModelType(ModelType.Y_WING);
        setPilotOrPassengerCapacity(2);
        setMatchingPilotFilter(Filters.Dutch);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new HasPilotingCondition(self, Filters.Dutch), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Dutch), 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__GOLD_1_V__TAKE_FORCE_PILE_CARD;

        if (GameConditions.hasPiloting(game, self, Filters.Dutch)
                && GameConditions.isOnceDuringEitherPlayersPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.hasForcePile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take top or bottom card of Force Pile into hand");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            List<StandardEffect> effectsToChoose = new ArrayList<StandardEffect>();
            effectsToChoose.add(new TakeCardIntoHandFromTopOfForcePileEffect(action, playerId) {
                @Override
                public String getText(SwccgGame game) {
                    return "Take top card of Force Pile into hand";
                }
            });
            effectsToChoose.add(new TakeCardIntoHandFromBottomOfForcePileEffect(action, playerId));
            action.appendEffect(
                    new ChooseEffectEffect(action, playerId, effectsToChoose));
            return Collections.singletonList(action);
        }
        return null;
    }
}
