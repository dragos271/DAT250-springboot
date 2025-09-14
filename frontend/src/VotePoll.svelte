<script>
  export let question;
  export let options = [];
  export let votes;
  export let onVote;

  function vote(optionId) {
    onVote(optionId);
  }
</script>

<div class="poll">
  <h2>{question}</h2>
  {#each options as option, i}
    <button on:click={() => vote(option.id)}>
      {option.caption} ({option.votes.length})
    </button>
  {/each}
</div>

<style>
  .poll button {
    margin: 0.5em 0;
    display: block;
    width: 100%;
    font-size: 1em;
    padding: 0.5em;
  }
</style>