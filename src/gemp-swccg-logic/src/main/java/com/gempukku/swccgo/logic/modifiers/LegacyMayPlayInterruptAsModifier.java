package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.conditions.Condition;

import java.util.LinkedList;
import java.util.List;

/**
 * A modifier that lets the affected Interrupts be played using game text granted by the source card, in addition to
 * their own game text. For example: "Look Sir, Droids and your Interrupts with 'advisor' in title may be played as
 * follows: 'USED: Use 1 Force to take an ISB agent into hand from Reserve Deck; reshuffle.'"
 *
 * The granted play is built by a {@link PlayMode} supplied by the source card. AbstractInterrupt offers the actions
 * returned by {@link #getGrantedPlayActions} alongside the Interrupt's other top-level plays.
 */
public class LegacyMayPlayInterruptAsModifier extends AbstractModifier {
    private final CardSubtype _playedAs;
    private final PlayMode _playMode;

    /**
     * Builds the play that the source card grants to an affected Interrupt.
     */
    public abstract static class PlayMode {
        /**
         * Creates the granted play of the Interrupt, or returns null if it may not be played that way right now.
         * The action must be created with the Interrupt as its card. The modifier sets the subtype it is played as
         * (for example CardSubtype.USED) once the action is built, so it goes to the right pile.
         * @param playerId the player playing the Interrupt
         * @param game the game
         * @param source the card granting the play
         * @param interrupt the Interrupt
         * @return the action, or null
         */
        public abstract PlayInterruptAction createAction(String playerId, SwccgGame game, PhysicalCard source, PhysicalCard interrupt);
    }

    /**
     * Creates a modifier that lets the affected Interrupts be played as the specified play mode.
     * @param source the source of the modifier
     * @param affectFilter the Interrupts that may be played this way
     * @param playedAs the Interrupt subtype the granted play is played as
     * @param playMode builds the granted play
     */
    public LegacyMayPlayInterruptAsModifier(PhysicalCard source, Filterable affectFilter, CardSubtype playedAs, PlayMode playMode) {
        this(source, null, affectFilter, playedAs, playMode);
    }

    /**
     * Creates a modifier that lets the affected Interrupts be played as the specified play mode while the condition is fulfilled.
     * @param source the source of the modifier
     * @param condition the condition, or null
     * @param affectFilter the Interrupts that may be played this way
     * @param playedAs the Interrupt subtype the granted play is played as
     * @param playMode builds the granted play
     */
    public LegacyMayPlayInterruptAsModifier(PhysicalCard source, Condition condition, Filterable affectFilter, CardSubtype playedAs, PlayMode playMode) {
        super(source, "May be played as granted by " + (source != null ? source.getTitle() : "another card"), affectFilter, condition, ModifierType.LEGACY__MAY_PLAY_INTERRUPT_AS);
        _playedAs = playedAs;
        _playMode = playMode;
    }

    /**
     * Gets the plays that other cards currently grant to the specified Interrupt.
     * @param playerId the player
     * @param game the game
     * @param interrupt the Interrupt
     * @return the actions (empty if none)
     */
    public static List<PlayInterruptAction> getGrantedPlayActions(String playerId, SwccgGame game, PhysicalCard interrupt) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        for (Modifier modifier : game.getModifiersQuerying().getModifiersAffectingCard(game.getGameState(), ModifierType.LEGACY__MAY_PLAY_INTERRUPT_AS, interrupt)) {
            if (modifier instanceof LegacyMayPlayInterruptAsModifier) {
                PhysicalCard source = modifier.getSource(game.getGameState());
                LegacyMayPlayInterruptAsModifier grant = (LegacyMayPlayInterruptAsModifier) modifier;
                PlayInterruptAction action = grant._playMode.createAction(playerId, game, source, interrupt);
                if (action != null) {
                    // Set last: PlayInterruptAction resets a single-subtype Interrupt to its printed subtype when its text is set
                    action.setPlayedAsSubtype(grant._playedAs);
                    actions.add(action);
                }
            }
        }
        return actions;
    }
}
