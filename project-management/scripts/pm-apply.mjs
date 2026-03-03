import fs from "fs";
import path from "path";
import matter from "gray-matter";
import yaml from "js-yaml";

const token = process.env.GITHUB_TOKEN;
const repoFull = process.env.REPO;

if (!token) throw new Error("Falta GITHUB_TOKEN");
if (!repoFull) throw new Error("Falta REPO");

const [owner, repo] = repoFull.split("/");

const COMMANDS_DIR = path.resolve("project-management/commands");

async function gh(method, url, body) {
  const res = await fetch(`https://api.github.com${url}`, {
    method,
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: "application/vnd.github+json",
      "Content-Type": "application/json",
    },
    body: body ? JSON.stringify(body) : undefined,
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(`${method} ${url} -> ${res.status}\n${text}`);
  }

  return res.status === 204 ? null : await res.json();
}

function extractYamlBlock(md) {
  const m = md.match(/```yaml\s*([\s\S]*?)\s*```/i);
  if (!m) throw new Error("No se encontró bloque yaml");
  return m[1];
}

async function ensureMilestone(spec) {
  const list = await gh("GET", `/repos/${owner}/${repo}/milestones?state=all`);
  const existing = list.find(m => m.title === spec.title);
  if (existing) return existing;

  return await gh("POST", `/repos/${owner}/${repo}/milestones`, {
    title: spec.title,
    description: spec.description || "",
  });
}

async function ensureLabel(name) {
  try {
    await gh("GET", `/repos/${owner}/${repo}/labels/${encodeURIComponent(name)}`);
  } catch {
    await gh("POST", `/repos/${owner}/${repo}/labels`, {
      name,
      color: "ededed"
    });
  }
}

async function run() {
  const files = fs.readdirSync(COMMANDS_DIR)
    .filter(f => f.endsWith(".md"));

  for (const file of files) {
    const filePath = path.join(COMMANDS_DIR, file);
    const raw = fs.readFileSync(filePath, "utf8");
    const parsed = matter(raw);

    if (parsed.data.executed_at) {
      console.log(`SKIP ${file} (ya ejecutado)`);
      continue;
    }

    const yamlBlock = extractYamlBlock(raw);
    const spec = yaml.load(yamlBlock);

    let milestoneNumber = null;
    let milestoneOriginalDescription = "";
    let sprintTotalEstimate = 0;
    let sprintIssueCount = 0;

    if (spec.milestone) {
      const ms = await ensureMilestone(spec.milestone);
      milestoneNumber = ms.number;
      milestoneOriginalDescription = ms.description || "";
    }

    if (Array.isArray(spec.labels)) {
      for (const l of spec.labels) {
        await ensureLabel(l);
      }
    }

    for (const op of spec.ops || []) {
      if (op.op === "create_issue") {

        sprintIssueCount++;
        if (op.estimate) {
          sprintTotalEstimate += Number(op.estimate);
        }

        for (const l of op.labels || []) {
          await ensureLabel(l);
        }

        const body = op.estimate
          ? `${op.body}\n\n---\nEstimate: ${op.estimate}`
          : op.body;

        const issue = await gh("POST", `/repos/${owner}/${repo}/issues`, {
          title: op.title,
          body,
          labels: op.labels,
          milestone: milestoneNumber
        });

        console.log(`Created issue #${issue.number}`);
      }
    }

    // Actualizar milestone con resumen
    if (milestoneNumber) {
      const summary = `

---

📊 **Resumen automático del sprint**

- Issues creadas: ${sprintIssueCount}
- Total estimado: ${sprintTotalEstimate} puntos
- Generado: ${new Date().toISOString()}
`;

      await gh("PATCH", `/repos/${owner}/${repo}/milestones/${milestoneNumber}`, {
        description: milestoneOriginalDescription + summary
      });
    }

    // Marcar archivo como ejecutado
    parsed.data.executed_at = new Date().toISOString();
    const updated = matter.stringify(parsed.content, parsed.data);
    fs.writeFileSync(filePath, updated, "utf8");

    console.log(`DONE ${file}`);
  }
}

run();