<script lang="ts">
	let message = "Let's play Planning Poker !";
</script>

<h1>{message}</h1>

<button class="button-play" on:click={() => window.location.href = '/play'}>Play</button>


<style>
    @import url('https://fonts.googleapis.com/css2?family=Cal+Sans&display=swap');

    h1 {
        text-align: center;
        margin-top: 3rem;
        color: #dedede;
        font-family: 'Cal Sans';
        font-size: 3rem;
        text-shadow: 0 0 10px #1F6838,
                     0 0 20px #1F6838;
    }

    .button-play {
        align-items: center;
        background-color: #dedede;
        border: 1px solid rgba(0, 0, 0, 0.1);
        border-radius: .25rem;
        box-shadow: rgba(0, 0, 0, 0.02) 0 1px 3px 0;
        box-sizing: border-box;
        color: rgba(0, 0, 0, 0.85);
        cursor: pointer;
        display: inline-flex;
        font-family: 'Cal Sans';
        font-size: 25px;
        justify-content: center;
        line-height: 1.25;
        margin: 0;
        min-height: 3rem;
        padding: calc(.875rem - 1px) calc(1.5rem - 1px);
        position: relative;
        text-decoration: none;
        transition: all 250ms;
        touch-action: manipulation;
        vertical-align: baseline;
        width: 20%;
        margin-top: 10%;
    }

    .button-play:hover,
    .button-play:focus {
        border-color: rgba(0, 0, 0, 0.15);
        box-shadow: rgba(0, 0, 0, 0.1) 0 4px 12px;
        color: rgba(0, 0, 0, 0.65);
    }

    .button-play:hover {
        transform: translateY(-2px);
    }

    .button-play:active {
        background-color: #F0F0F1;
        border-color: rgba(0, 0, 0, 0.15);
        box-shadow: rgba(0, 0, 0, 0.06) 0 2px 4px;
        color: rgba(0, 0, 0, 0.65);
        transform: translateY(0);
    }
</style>