package com.gempukku.swccgo.cards.set1106.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Effect
 * Subtype: Immediate
 * Title: I Had No Choice
 */
public class Card1106_037 extends AbstractImmediateEffect {
    public Card1106_037() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, Title.I_Had_No_Choice, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("Gamblers are vulnerable to bribery, extortion and other forms of manipulation. Their notorious dealings can be easily taken advantage of.");
        setGameText("During any deploy phase, deploy on opponent's [Maintenance] Lando. Lando's game text is canceled. During your control phase, may randomly retrieve 1 Force.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return GameConditions.isDuringEitherPlayersPhase(game, Phase.DEPLOY);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.Lando, Filters.icon(Icon.MAINTENANCE));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.hasAttached(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__I_HAD_NO_CHOICE__RETRIEVE_FORCE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.hasLostPile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Randomly retrieve 1 Force");
            action.setActionMsg("Randomly retrieve 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
