package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set7.dark.Card7_258;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;
import java.util.Collections;
import java.util.List;

public class Card15_269 extends Card7_258 {
    public Card15_269() {
        setCardSubtype(CardSubtype.LOST);
        setAlternateDestiny(2);
        setLore("");
        setGameText("During your deploy phase, target a system where your total power is more than double opponent's total power and opponent has no Jedi or starship weapon. Place all opponent's starships there (and cards on them) in owner's Used Pile. OR If opponent just deployed a planet site, deploy the related system from your Reserve Deck; reshuffle.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent("Overwhelmed",Uniqueness.UNRESTRICTED),new TitleComponent("A Dark Time For The Rebellion",Uniqueness.UNIQUE));
    }
    @Override public String getTitle() { return "Overwhelmed & A Dark Time For The Rebellion"; }
    @Override public Float getDestiny() { return 2f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.A_DARK_TIME_FOR_THE_REBELLION__DOWNLOAD_RELATED_SYSTEM;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, opponent, Filters.planet_site)) {
            PhysicalCard cardPlayed = ((PlayCardResult) effectResult).getPlayedCard();
            final String systemName = cardPlayed.getPartOfSystem();
            if (systemName != null) {
                if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, systemName, true)) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                    action.setText("Deploy " + systemName + " system from Reserve Deck");
                    // Allow response(s)
                    action.allowResponses(
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.system, Filters.title(systemName)), true));
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
