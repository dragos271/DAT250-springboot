<script>
  export let onCreate;

  let question = "";
  let options = ["", ""];
  
  function addOption() {
    options = [...options, ""];
  }

  function updateOption(optionId, value) {
    options[optionId] = value;
  }

  function createPoll() {
    const filteredOptions = options.map(o => o.trim()).filter(o => o);
    if (question.trim() && filteredOptions.length >= 2) {
      onCreate({ question: question.trim(), options: filteredOptions });
      question = "";
      options = ["", ""];
    }
  }
</script>

<div class="create-poll">
  <h2>Create a New Poll</h2>
  <input
    placeholder="Poll question"
    bind:value={question}
  />
  <div>
    {#each options as {}, i}
      <input
        placeholder={`Option ${i + 1}`}
        bind:value={options[i]}
        on:input={e => updateOption(i, e.target.value)}
      />
    {/each}
  </div>
  <button on:click={addOption}>Add Option</button>
  <button on:click={createPoll} disabled={!question.trim() || options.filter(o => o.trim()).length < 2}>
    Create Poll
  </button>
</div>

<style>
  .create-poll input {
    display: block;
    margin: 0.5em 0;
    width: 100%;
    padding: 0.5em;
  }
  .create-poll button {
    margin-right: 0.5em;
    margin-top: 1em;
  }
</style>